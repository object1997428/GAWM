import axios from "axios";
import React, { Component } from "react";
// import "./App.css";
import "./AppCustom.css";
import { OpenVidu } from "openvidu-browser";
import UserVideoComponent from "../UserVideoComponent.jsx";
import UserModel from "../models/user-model.jsx";
import ChatComponent from "../chat/ChatComponent.jsx";
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
class EnterLive extends Component {
  constructor(props) {
    super(props);

    this.state = {
      mySessionId: this.props.sessionId,
      myUserName: this.props.user ? this.props.user.nickname : "none",
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      liveName: this.props.title,
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
    this.onChat = this.onChat.bind(this);
    this.handleChangeSubscribers = this.handleChangeSubscribers.bind(this);
    this.handleRedirect = this.handleRedirect.bind(this);
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

  handleMainVideoStream(stream) {
    if (this.state.mainStreamManager !== stream) {
      this.setState({
        mainStreamManager: stream,
      });
      localUser.setStreamManager(stream);
    }
  }

  handleChangeSubscribers(e) {
    this.setState({
      subscribers: e.target.value,
    });
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
    console.log("mySessionId", this.state.mySessionId);
    console.log("this.props", this.props);
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

          // const data = { name: "subscriber", data: subscriber };
          // console.log(data);
          if (subscriber.stream != null) {
            console.log("담았다!");
            this.handleMainVideoStream(subscriber);
            const data = { name: "담은거", subscriber };
            console.log(data);
          }

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

        mySession
          .connect(this.state.token, { clientData: this.state.myUserName })
          .then(async () => {
            localUser.setNickname(this.state.myUserName);
            localUser.setConnectionId(this.state.session.connection.connectionId);
            localUser.setScreenShareActive(true);
            // localUser.setStreamManager(publisher);
            localUser.setType("remote");
            localUser.setAudioActive(true);
            localUser.setVideoActive(true);
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
      session: undefined,
      subscribers: [],
      mySessionId: "SessionA",
      myUserName: "Participant" + Math.floor(Math.random() * 100),
      mainStreamManager: undefined,
      publisher: undefined,
      isPublic: undefined,
      deleted: undefined,
      liveName: undefined,
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

  onChat(property) {
    let display = property;
    console.log("chat", display);
    this.setState({ chatDisplay: display });
  }

  render() {
    const mySessionId = this.state.mySessionId;
    const myUserName = this.state.myUserName;
    const isPublic = this.state.isPublic;
    const liveName = this.state.liveName;
    const deleted = this.state.deleted;
    var chatDisplay = { display: this.state.chatDisplay };
    const appliedImg = "https://gwwmbucket.s3.ap-northeast-2.amazonaws.com/" + this.props.image;

    return (
      <div className="container">
        {this.state.session === undefined ? (
          <form className="form-group" onSubmit={this.joinSession}>
            <div id="join" className="w-26 h-26 rounded-lg relative">
              <img
                className="w-full h-full object-cover rounded-lg"
                src={appliedImg}
                alt={this.props.title}
              />
              <div
                id="join-dialog"
                className="absolute bottom-0 left-0 right-0 h-9 bg-black opacity-70 rounded-b-lg leading-[0.5rem] px-0.5"
              >
                <span className="inline-block text-sm text-white">{this.props.title}</span>
                <span id="two" className="inline-block text-[0.6rem] text-tertiary">
                  {this.props.createdDate} · {this.props.points} 포인트
                </span>

                <p className="text-center">
                  <input
                    className="btn btn-lg btn-success"
                    name="commit"
                    type="submit"
                    value="JOIN"
                  />
                </p>
              </div>
            </div>
          </form>
        ) : null}

        {this.state.session !== undefined ? (
          <div id="session">
            <div id="session-header">
              <h1 id="session-title">{mySessionId}</h1>
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
              {this.state.subscribers.map((sub, i) => (
                <div
                  key={sub.id}
                  className="stream-container col-md-6 col-xs-6"
                  onClick={() => this.handleMainVideoStream(sub)}
                >
                  <span>{sub.id}</span>
                  <UserVideoComponent streamManager={sub} />
                </div>
              ))}
              {this.state.mainStreamManager !== undefined ? (
                <div className="OT_root OT_publisher custom-class" style={chatDisplay}>
                  <ChatComponent
                    user={localUser}
                    myStream={this.mainStreamManager}
                    chatDisplay={this.state.chatDisplay}
                    close={this.toggleChat}
                    messageReceived={this.checkNotification}
                  />
                </div>
              ) : null}
            </div>
          </div>
        ) : null}
      </div>
    );
  }

  async getToken() {
    return await this.createToken(this.state.mySessionId);
  }

  async createSession(sessionId, liveName, isPublic, deleted) {
    const response = await axios.post(
      APPLICATION_SERVER_URL + "gawm/back/api/sessions",
      {
        customSessionId: sessionId,
        liveName: liveName,
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

export default withUser(EnterLive);
