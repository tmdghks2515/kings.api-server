# api-server working rules

- Use Lombok `@RequiredArgsConstructor` instead of manual constructors for Spring bean injection.
- Use Java `var` for local variables when the initializer makes the type obvious.
- Do not use method-level `@ResponseStatus`; return `ResponseEntity` when a controller needs an explicit HTTP status.
- Do not run build or test commands after changes unless the user explicitly asks for verification, because they are slow in this project.
- Name DTO classes with `Command`, `Query`, or `Data` suffixes: use `Command` for write requests, `Query` for read/filter requests, and `Data` for response payloads.
