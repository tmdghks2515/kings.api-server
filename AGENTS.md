# api-server 작업 규약

- Spring Bean 주입은 직접 생성자를 작성하지 말고 Lombok `@RequiredArgsConstructor`를 사용한다.
- 초기화 값만으로 타입이 명확한 지역 변수는 Java `var`를 사용한다.
- 성공 응답은 별도 상태값을 지정하지 않는다. 성공이면 기본 200 응답을 사용하고, 컨트롤러에서 성공 응답용 `@ResponseStatus`, `ResponseEntity.status(...)`, `ResponseEntity.noContent()`를 사용하지 않는다.
- 변경 후 빌드, 컴파일, 테스트 명령은 실행하지 않는다. 이 프로젝트에서는 오래 걸리므로 사용자가 명시적으로 검증을 요청한 경우에만 실행한다.
- DTO 클래스 이름은 `Command`, `Query`, `Data` 접미사를 사용한다. 쓰기 요청은 `Command`, 조회/필터 요청은 `Query`, 응답 페이로드는 `Data`를 사용한다.
- 여러 건을 처리할 수 있는 작업은 가능한 경우 개별 반복 처리 대신 일괄 처리 API 또는 bulk query를 우선 사용한다.
