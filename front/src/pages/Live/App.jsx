import axios from "axios";
import React, { Component } from "react";
import "./App.css";
import UserVideoComponent from "./UserVideoComponent.jsx";
import { OpenVidu } from "openvidu-browser";
import UserModel from "./models/user-model.jsx";
import ChatComponent from "./chat/ChatComponent.jsx";
import { useNavigate, Outlet } from "react-router-dom";
import { useUserStore, fetchUserInfo } from "@/stores/user.js";

var localUser = new UserModel();
const APPLICATION_SERVER_URL =
  process.env.NODE_ENV === "production" ? "" : "http://localhost:8080/";

function withUser(Component) {
  return function WrappedComponent(props) {
    const user = useUserStore((state) => state.user);
    return <Component {...props} user={user} />;
  };
}

class Live extends Component {
  constructor(props) {
    super(props);

    // console.log("props", this.props.user);

    this.state = {
      mySessionId: "SessionA",
      myUserName: this.props.user ? this.props.user.nickname : "none",
      myPoint: 10,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      liveName: "26C 라이브 이름",
      isPublic: true,
      deleted: false,
      token: "initial token",
      localUser: undefined,
      chatDisplay: "block",
      accessAllowed: false,
    };

    // Bind this to the event handlers
    this.joinSession = this.joinSession.bind(this);
    this.leaveSession = this.leaveSession.bind(this);
    this.switchCamera = this.switchCamera.bind(this);
    this.handleChangeSessionId = this.handleChangeSessionId.bind(this);
    this.handleChangeUserName = this.handleChangeUserName.bind(this);
    this.handleMainVideoStream = this.handleMainVideoStream.bind(this);
    this.onbeforeunload = this.onbeforeunload.bind(this);
    this.handleChangeIspublic = this.handleChangeIspublic.bind(this);
    this.handleChangeLiveName = this.handleChangeLiveName.bind(this);
    this.handleChangeDeleted = this.handleChangeDeleted.bind(this);
    this.handleChangeToken = this.handleChangeToken.bind(this);
    this.toggleChat = this.toggleChat.bind(this);
    this.handleRedirect = this.handleRedirect.bind(this);
    this.handleBack = this.handleBack.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.componentDidUpdate = this.componentDidUpdate.bind(this);
  }

  componentDidUpdate(prevProps) {
    // user props가 변경되었을 때만 업데이트합니다.
    if (prevProps.user !== this.props.user) {
      this.setState({
        myUserName: this.props.user.nickname, // 새로운 user 값으로 상태를 업데이트합니다.
        // 다른 필요한 업데이트도 수행할 수 있습니다.
      });
    }
  }

  componentDidMount() {
    window.addEventListener("beforeunload", this.onbeforeunload);
  }

  componentWillUnmount() {
    window.removeEventListener("beforeunload", this.onbeforeunload);
  }

  onbeforeunload(event) {
    this.leaveSession();
  }

  handleRedirect() {
    window.location.href = "/gawm/";
  }

  handleBack() {
    this.props.history.push("/gawm");
  }

  handleChangeSessionId(e) {
    this.setState({
      mySessionId: e.target.value,
    });
  }

  handleChangeLiveName(e) {
    this.setState({
      liveName: e.target.value,
    });
  }

  handleChangeLivePoint(e) {
    this.setState({
      myPoint: e.target.value,
    });
  }

  handleChangeToken(e) {
    this.setState({
      token: e.target.value,
    });
  }

  handleChangeIspublic(e) {
    this.setState({
      isPublic: e.target.value,
    });
  }

  handleChangeDeleted(event) {
    const isChecked = event.target.checked;
    this.setState({ deleted: isChecked });
  }

  handleChangeUserName(e) {
    this.setState({
      myUserName: e.target.value,
    });
  }

  handleCancel() {
    const navigate = useNavigate();
    navigate("/landing");
  }

  handleMainVideoStream(stream) {
    if (this.state.mainStreamManager !== stream) {
      this.setState({
        mainStreamManager: stream,
      });
    }
  }

  deleteSubscriber(streamManager) {
    let subscribers = this.state.subscribers;
    let index = subscribers.indexOf(streamManager, 0);
    if (index > -1) {
      subscribers.splice(index, 1);
      this.setState({
        subscribers: subscribers,
      });
    }
  }

  async joinSession() {
    event.preventDefault();
    if (this.state.mySessionId && this.state.myUserName) {
      const token = await this.getToken();
      console.log(token);
      this.setState({
        token: token,
        session: true,
      });
    }

    this.OV = new OpenVidu();

    this.setState(
      {
        session: this.OV.initSession(),
      },
      async () => {
        var mySession = this.state.session;

        mySession.on("streamCreated", (event) => {
          var subscriber = mySession.subscribe(event.stream, undefined);
          var subscribers = this.state.subscribers;
          subscribers.push(subscriber);

          this.setState({
            subscribers: subscribers,
          });
        });

        mySession.on("streamDestroyed", (event) => {
          this.deleteSubscriber(event.stream.streamManager);
        });

        mySession.on("exception", (exception) => {
          console.warn(exception);
        });

        // const token = await this.getToken();

        mySession
          .connect(this.state.token, { clientData: this.state.myUserName })
          .then(async () => {
            let publisher = await this.OV.initPublisherAsync(undefined, {
              audioSource: undefined,
              videoSource: undefined,
              publishAudio: true,
              publishVideo: true,
              resolution: "640x480",
              frameRate: 30,
              insertMode: "APPEND",
              mirror: false,
            });

            mySession.publish(publisher);

            localUser.setNickname(this.state.myUserName);
            localUser.setConnectionId(this.state.session.connection.connectionId);
            localUser.setScreenShareActive(true);
            localUser.setStreamManager(publisher); //<-여기 이 함수로 publisher자리에 sub 넣으면 됨
            localUser.setType("remote");
            localUser.setAudioActive(true);
            localUser.setVideoActive(true);

            var devices = await this.OV.getDevices();
            var videoDevices = devices.filter((device) => device.kind === "videoinput");
            var currentVideoDeviceId = publisher.stream
              .getMediaStream()
              .getVideoTracks()[0]
              .getSettings().deviceId;
            var currentVideoDevice = videoDevices.find(
              (device) => device.deviceId === currentVideoDeviceId
            );

            this.setState({
              currentVideoDevice: currentVideoDevice,
              mainStreamManager: publisher,
              publisher: publisher,
            });
          })
          .catch((error) => {
            console.log("There was an error connecting to the session:", error.code, error.message);
          });
      }
    );
  }

  leaveSession() {
    const mySession = this.state.session;

    if (mySession) {
      mySession.disconnect();
    }

    this.OV = null;
    this.setState({
      mySessionId: "SessionA",
      myUserName: "은은한 달",
      myPoint: 10,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      liveName: "26C 라이브",
      isPublic: true,
      deleted: false,
      token: "initial token",
      localUser: undefined,
      chatDisplay: "none",
      accessAllowed: false,
    });
  }

  async switchCamera() {
    try {
      const devices = await this.OV.getDevices();
      var videoDevices = devices.filter((device) => device.kind === "videoinput");

      if (videoDevices && videoDevices.length > 1) {
        var newVideoDevice = videoDevices.filter(
          (device) => device.deviceId !== this.state.currentVideoDevice.deviceId
        );

        if (newVideoDevice.length > 0) {
          var newPublisher = this.OV.initPublisher(undefined, {
            videoSource: newVideoDevice[0].deviceId,
            publishAudio: true,
            publishVideo: true,
            mirror: true,
          });

          await this.state.session.unpublish(this.state.mainStreamManager);
          await this.state.session.publish(newPublisher);
          this.setState({
            currentVideoDevice: newVideoDevice[0],
            mainStreamManager: newPublisher,
            publisher: newPublisher,
          });
        }
      }
    } catch (e) {
      console.error(e);
    }
  }

  toggleChat(property) {
    let display = property;

    if (display === undefined) {
      display = this.state.chatDisplay === "none" ? "block" : "none";
    }
    if (display === "block") {
      this.setState({ chatDisplay: display, messageReceived: false });
    } else {
      console.log("chat", display);
      this.setState({ chatDisplay: display });
    }
  }

  render() {
    const mySessionId = this.state.mySessionId;
    const myUserName = this.state.myUserName;
    const isPublic = this.state.isPublic;
    const liveName = this.state.liveName;
    const deleted = this.state.deleted;
    var chatDisplay = { display: this.state.chatDisplay };
    // const navigate = useNavigate();

    return (
      <div className="container">
        {this.state.session === undefined ? (
          <div id="join">
            <div id="img-div">
              <img src="resources/images/openvidu_grey_bg_transp_cropped.png" alt="OpenVidu logo" />
            </div>
            <div id="join-dialog" className="jumbotron vertical-center">
              <h1> 26도씨 라이브 생성 </h1>
              <h1> 방정보 </h1>
              <form className="form-group" onSubmit={this.joinSession}>
                {/* <p>
                  <label>Participant: </label>
                  <input
                    className="form-control"
                    type="text"
                    id="userName"
                    value={myUserName}
                    onChange={this.handleChangeUserName}
                    required
                  />
                </p> */}
                <p>
                  <label> Session: </label>
                  <input
                    className="form-control"
                    type="text"
                    id="sessionId"
                    value={mySessionId}
                    onChange={this.handleChangeSessionId}
                    required
                  />
                </p>
                <p>
                  <label> 제목: </label>
                  <input
                    className="form-control"
                    type="text"
                    id="liveName"
                    value={liveName}
                    onChange={this.handleChangeLiveName}
                    required
                  />
                </p>
                <p>
                  <label> 감포인트: </label>
                  <input
                    className="form-control"
                    type="number"
                    id="livePoint"
                    value={this.state.myPoint}
                    onChange={this.handleChangeLivePoint}
                    required
                  />
                </p>
                <p>
                  <label> isPublic : </label>
                  <label class="switch">
                    <input
                      type="checkbox"
                      id="isPublic"
                      checked={this.state.isPublic}
                      onChange={this.handleChangeIspublic}
                    />
                    <span class="slider"></span>
                  </label>
                </p>

                <p>
                  <label> 세션 비우기 : </label>
                  <label class="switch">
                    <input
                      type="checkbox"
                      id="deleted"
                      checked={this.state.deleted}
                      onChange={this.handleChangeDeleted}
                    />
                    <span class="slider"></span>
                  </label>
                </p>

                <p className="text-center">
                  <input
                    className="btn btn-lg btn-success"
                    name="commit"
                    type="submit"
                    value="라이브 시작"
                  />
                </p>
              </form>
              <p className="text-center">
                <button onClick={this.handleRedirect}>취소</button>
              </p>
              {/* <p>
                <button onClick={this.handleBack}>리다이렉트111</button>
              </p>
              <p>
                <input
                  className="btn btn-lg btn-success"
                  name="commit"
                  type="submit"
                  value="취소"
                  onClick={this.handleCancel}
                />
              </p> */}
            </div>
          </div>
        ) : null}

        {this.state.session !== undefined ? (
          <div id="session">
            <div id="session-header">
              {/* <h1 id="session-title">{mySessionId}</h1> */}
              <input
                className="btn btn-large btn-danger"
                type="button"
                id="buttonLeaveSession"
                onClick={this.leaveSession}
                value="Leave session"
              />
              <input
                className="btn btn-large btn-success"
                type="button"
                id="buttonSwitchCamera"
                onClick={this.switchCamera}
                value="Switch Camera"
              />
            </div>

            <div id="video-container" className="col-md-6">
              {this.state.publisher !== undefined ? (
                <div
                  className="stream-container col-md-6 col-xs-6"
                  onClick={() => this.handleMainVideoStream(this.state.publisher)}
                >
                  <UserVideoComponent streamManager={this.state.publisher} />
                </div>
              ) : null}
              {this.state.mainStreamManager !== undefined && (
                <div className="OT_root OT_publisher custom-class" style={chatDisplay}>
                  <ChatComponent
                    user={localUser}
                    chatDisplay={this.state.chatDisplay}
                    close={this.toggleChat}
                    messageReceived={this.checkNotification}
                  />
                </div>
              )}
            </div>
          </div>
        ) : null}
      </div>
    );
  }

  async getToken() {
    const sessionId = await this.createSession(
      this.state.mySessionId,
      this.state.liveName,
      this.state.myPoint,
      this.state.isPublic,
      this.state.deleted
    );
    return await this.createToken(this.state.mySessionId);
  }

  async createSession(sessionId, liveName, livePoint, isPublic, deleted) {
    const response = await axios.post(
      APPLICATION_SERVER_URL + "gawm/back/api/sessions",
      {
        customSessionId: sessionId,
        liveName: liveName,
        livePoint: livePoint,
        isPublic: isPublic,
        deleted: deleted,
      },
      {
        headers: { "Content-Type": "application/json" },
        withCredentials: true,
      }
    );
    return response.data;
  }

  async createToken(liveRoomId) {
    const response = await axios.post(
      APPLICATION_SERVER_URL + "gawm/back/api/sessions/" + liveRoomId + "/connections",
      { customSessionId: liveRoomId },
      {
        headers: { "Content-Type": "application/json" },
        withCredentials: true,
      }
    );
    console.log(response);
    return response.data;
  }
}

export default withUser(Live);
