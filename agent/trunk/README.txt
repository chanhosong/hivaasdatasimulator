
# How to install

1) Download & Build
- bamboo 의 최신 빌드 history 의 Artifacts 에서 zip file 다운로드 가능.
- Eclipse 에서 최신소스로 update 후 package maven goals 를 실행하면 target/vdip-agent-*-bin.zip 가 생성됨.

2) install
- vdip-agent-*-bin.zip 파일을 적당한 위치(위치 상관없음)에 압축해제하면 끝. 
- 주의 : jdk 1.7.* 이상이 path 에 등록되어 있어야 함. 환경변수 JAVA_HOME 으로 특정 JDK 를 지정할수도 있음. 

# How to run
- run startup.bat on Windows
- run startup.sh on Linux

# Test script
agent 설치후 정상 작동여부를 테스트하기 위해 test client 를 실행해볼수 있다.
- run test.bat on Windows
- run test.sh on Linux

3) install as Service on Windows
- vdip-agent-*-bin.zip 파일 압축 해제후
- run installService.bat


# 이클립스(Eclipse) Amateras UML 플러그인 설치
- 다운로드 페이지 : http://sourceforge.jp/projects/amateras/releases/

1) AmaterasUML_1.3.4.zip 파일 다운로드
2) 압축을 푼후 jar 파일들을 eclipse > plugins 폴더안에 넣고
3) eclipse 재시작하면 끝.

