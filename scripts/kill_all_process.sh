#!/bin/bash
# 4443 도커 프로세스 kill
# sudo docker container stop $(sudo docker container ls -q --filter "publish=4443")

# 8080번 포트에 해당하는 PID 찾기
PID_8080=$(sudo lsof -t -i:8080)

# 8080번 포트에 해당하는 프로세스가 있는지 확인
if [ -z "$PID_8080" ]; then
    echo "포트 8080을 사용하는 프로세스가 실행 중이지 않습니다."
else
    # 해당 프로세스 종료
    sudo kill -9 $PID_8080
    echo "포트 8080을 사용하는 프로세스를 종료했습니다."
fi

# 8000번 포트에 해당하는 PID 찾기
PID_8000=$(sudo lsof -t -i:8000)

# 8000번 포트에 해당하는 프로세스가 있는지 확인
if [ -z "$PID_8000" ]; then
    echo "포트 8000을 사용하는 프로세스가 실행 중이지 않습니다."
else
    # 해당 프로세스 종료
    sudo kill -9 $PID_8000
    echo "포트 8000을 사용하는 프로세스를 종료했습니다."
fi
