import React, { useState, useEffect } from 'react';
import axios from 'axios';
import logoImage from '../../assets/images/HomeLogo.svg';
import LiveImg from './LiveImg.png';
import TodayLookComponent from './TodayLookComponent.jsx';
import LiveComponent from './LiveComponent.jsx';
import {get_top_list} from '../../apis/lookbook'
import { gawmApiAxios } from '../../utilities/http-commons';
const gawmapiAxios = gawmApiAxios()
export default function Browse() {
    const [todayLooks, setTodayLooks] = useState([]);
    const [liveRooms, setLiveRooms] = useState([]);

    useEffect(() => {
        const fetchTodayLooks = async () => {
            try {
                const response = await get_top_list();
                setTodayLooks(response.data.content);
            } catch (error) {
                console.error('Today Looks 데이터를 불러오는데 실패했습니다.', error);
            }
        };

        fetchTodayLooks();
    }, []);

    useEffect(() => {
        const fetchLiveRooms = async () => {
            try {
                const response = await gawmapiAxios.get('/live-room/follow/');
                console.log(response)
                setLiveRooms(response.data.content);
            } catch (error) {
                console.error('Live Rooms 데이터를 불러오는데 실패했습니다.', error);
            }
        };
        fetchLiveRooms();
    }, []);



    const Header = () => {
        return (
            <div className="fixed mt-1.5 ml-2.5">
                <img src={logoImage} alt="Logo" className="w-auto display: block" />
            </div>
        );
    }

    const TodayLookSection = ({ title }) => {
        return (
            <div className="today-look-section mt-4">
                <h2 className="h2-nps">{title}</h2>
                <div className="grid grid-cols-2 gap-4 px-4">
                    {todayLooks?.slice(0, 2).map((look) => (
                        <TodayLookComponent
                            key={look.lookbook_id}
                            lookImage={look.lookbook_img}
                            userId={look.user_id}
                            profileImage={look.profile_img}
                        />
                    ))}
                </div>
            </div>
        );
    };


    const LiveSection = ({ title }) => {
        return (
            <div className="live-section mt-4">
                <div className="flex items-center">
                    <h2 className="h2-nps">{title}</h2>
                    <img src={LiveImg} alt="Live" className="ml-2 w-10 h-6" />
                </div>

                <div className="flex gap-2 mt-1 justify-center">
                    {liveRooms.map(room => (
                        <LiveComponent
                            key={room.user_id}
                            image={room.profile_img}
                            title={room.name}
                            createdDate={new Date(room['시작시간'])}
                            points={room.point}
                        />
                    ))}
                </div>
            </div>
        );
    };
    

    return (
        <div className="min-h-screen flex flex-col">
            <Header />
            <div className="flex-1 mt-9">
                <TodayLookSection title="오늘의 감각" />
                <LiveSection title="26˚C 라이브" />
                <TodayLookSection title="내감어때" />
            </div>
        </div>
    );
}
