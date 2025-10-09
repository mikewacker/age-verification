ARG GRADLE_TAG
ARG TEMURIN_JRE_TAG

FROM gradle:${GRADLE_TAG} AS build
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/home/gradle/.gradle \
    --mount=type=cache,target=/src/.gradle \
    --mount=type=cache,target=/src/buildSrc/.gradle \
    --mount=type=cache,target=/src/buildSrc/build \
    gradle installDist

FROM eclipse-temurin:${TEMURIN_JRE_TAG} AS site
WORKDIR /app
COPY --from=build /src/site/app/build/install/app/ /app/
ENTRYPOINT ["/app/bin/app"]

FROM eclipse-temurin:${TEMURIN_JRE_TAG} AS avs
WORKDIR /app
COPY --from=build /src/avs/app/build/install/app/ /app/
ENTRYPOINT ["/app/bin/app"]
