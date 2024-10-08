import AdaptiveContainer from "../../../components/AdaptiveContainer";
import CenteredTopBar from "../CenteredTopBar";
import ListGroup from "../../../components/ListGroup";
import ListItem from "../../../components/ListGroup/ListItem";
import { deleteAccount, fetchUserInfo, useUserStore } from "../../../stores/user";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { logout, updateProfileImge } from "../../../apis/user";

/**
 * 마이페이지 내 설정 페이지를 생성합니다
 * 
 * @returns 설정 페이지 component
 */
export default function Settings() {
	const {
		profileImg, nickname, gender, age, provider
	} = useUserStore(
		(state) => (
			{
				"profileImg": state.user?.profileImg,
				"nickname": state.user?.nickname,
				"gender": state.user?.gender,
				"age": state.user?.age,
				"provider": state.user?.provider
			}
		)
	);

	const navigate = useNavigate();
	useEffect(
		() => {
			// 속성이 준비되지 않았을 시 마이페이지로 되돌리기
			if(
				typeof profileImg === "undefined" ||
				typeof nickname === "undefined" ||
				typeof gender === "undefined" ||
				typeof age === "undefined" ||
				typeof provider === "undefined"
			) navigate("/mypage");
		},
		[profileImg, nickname, gender, age, provider]
	);

	const genderNames = {
		"FEMALE": "여성",
		"MALE": "남성"
	};

	// 로그아웃 시도 시 처리
	const onLogout = async () => {
		await logout();
		navigate("/landing");
	}

	// 회원 탈퇴 시도 시 처리
	const onAccountDeletion = async () => {
		if(!window.confirm("정말 탈퇴하시겠습니까?"))
			return;
		
		try {
			deleteAccount(provider);
			navigate("/landing");
		}
		catch(error) {}
	};

	// 프로필 사진 업데이트를 위한 사진 <input>
	const profileImageInput = useRef(null);
	const selectProfileImage = () => {
		profileImageInput.current.click();
	};
	const changeProfileImage = async (event) => {
		const file = event.target.files[0];
		if(!file || !file.type.startsWith("image/"))
			return;

		try {
			const formData = new FormData();
			formData.append("multipartFile", file);
			await updateProfileImge(formData);
			await fetchUserInfo();
		}
		catch(error) { console.error(error); }
	};

	return (
		<>
			{/* 계정 설정 */}
			<ListItem className="mt-4 pt-2 pb-1 text-[#767676] text-sm" noHover div>
				계정 설정
			</ListItem>
			<ListGroup div>
				<ListItem div className="cursor-pointer flex flex-row gap-4 items-center py-2" onClick={selectProfileImage}>
					<span className="grow">프로필 사진 변경</span>
					<input className="hidden" type="file" accept="image/*" ref={profileImageInput} onChange={changeProfileImage} />
					<div
						style={{"--image-url": `url(${import.meta.env.VITE_CLOTHES_BASE_URL}/${profileImg})`}}
						className={`size-12 aspect-square rounded-full ${profileImg ? "bg-[image:var(--image-url)] bg-cover bg-center bg-no-repeat" : "bg-transparent"}`}
					></div>
				</ListItem>
				{/* 닉네임 변경 */}
				<ListItem link href="/mypage/settings/nickname" className="flex flex-row gap-4">
					<span className="grow">닉네임 설정</span>
					<span className="text-[#767676]">{nickname}</span>
				</ListItem>
				<ListItem link href="/mypage/settings/gender" className="flex flex-row gap-4">
					<span className="grow">성별 설정</span>
					<span className="text-[#767676]">{gender in genderNames ? genderNames[gender] : ""}</span>
				</ListItem>
				<ListItem link href="/mypage/settings/age" className="flex flex-row gap-4">
					<span className="grow">나이 설정</span>
					<span className="text-[#767676]">{age ? `${age}세` : ""}</span>
				</ListItem>
			</ListGroup>

			{/* 로그아웃 / 회원탈퇴 */}
			<ListItem className="mt-4 pt-2 pb-1 text-[#767676] text-sm" noHover div>
				기타
			</ListItem>
			{/* TODO: 로그아웃, 회원탈퇴 링크 추가 */}
			<ListGroup div>
				<ListItem div className="cursor-pointer" onClick={onLogout}>
					로그아웃
				</ListItem>
				<ListItem div className="cursor-pointer" onClick={onAccountDeletion}>
					회원탈퇴
				</ListItem>
			</ListGroup>
		</>
	)
}
