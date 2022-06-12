# gradle-private-repository
이곳에서는 private repository 를 구성하고 gradle 에서 repository를 연결하는 실습을 합니다.
private repository 의 저장소로는 AWS s3 를 이용합니다

## Directory

- `lib-provider`
  - 라이브러리를 만들고 s3 에 업로드하는 라이브러리 제공자
- `client`
  - s3 에 올라간 라이브러리를 gradle repository 로 연결해서 사용하는 사용자
