import React from 'react';
import kakao_login from '../../assets/images/kakao_login.svg';

const SocialKakao = () => {

    const redirect_uri = 'http://localhost:4000/gawm/'; // Redirect URI
    // oauth 요청 URL
    const kakaoURL = `http://localhost:8080/gawm/back/oauth2/authorization/kakao?redirect_uri=${redirect_uri}`;

    const handleLogin = () => {
        window.location.href = kakaoURL;
    };

    return (
        <>
            <div className="flex justify-center items-center">
                <img src={kakao_login} alt="카카오 로그인" onClick={handleLogin} className="cursor-pointer" />
            </div>
        </>
    );
};


export default SocialKakao;
