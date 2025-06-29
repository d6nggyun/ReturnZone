package _ITHON.ReturnZone.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // API 기본 정보
        Info info = new Info()
                .title("ReturnZone")
                .description("ReturnZone - 분실물 반환 중개 서비스")
                .version("1.0.0");

        // 서버 정보
        Server server = new Server()
                .url("http://15.164.234.32")
                .description("배포 서버");

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버");

        // OpenAPI 객체 구성
        return new OpenAPI()
                .info(info)
                .servers(List.of(server, localServer));
    }
}
