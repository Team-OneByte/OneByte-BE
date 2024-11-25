package classfit.example.classfit.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerController {
    @GetMapping("/swagger-ui/index.html")
    public String swaggerUi() {
        return "<!-- HTML for static distribution bundle build -->\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <meta http-equiv=\"Content-Security-Policy\" content=\"upgrade-insecure-requests\" />\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Swagger UI</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./swagger-ui.css\" />\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"index.css\" />\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"./favicon-32x32.png\" sizes=\"32x32\" />\n" +
                "    <link rel=\"icon\" type=\"image/png\" href=\"./favicon-16x16.png\" sizes=\"16x16\" />\n" +
                "  </head>\n" +
                "\n" +
                "  <body>\n" +
                "    <div id=\"swagger-ui\"></div>\n" +
                "    <script src=\"./swagger-ui-bundle.js\" charset=\"UTF-8\"> </script>\n" +
                "    <script src=\"./swagger-ui-standalone-preset.js\" charset=\"UTF-8\"> </script>\n" +
                "    <script src=\"./swagger-initializer.js\" charset=\"UTF-8\"> </script>\n" +
                "  </body>\n" +
                "</html>\n";
    }
}

