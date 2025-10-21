package K23CNT1.Tta.Day02.ioc_spring;

import org.springframework.stereotype.Service;

@Service
public class GreetingServiceImpl implements GreetingService {
    public String greet(String name) {
        return "<h2>Devmaster[Spring Boot] Xin chào,</h2>" +
                "<h2>" + name + "</h2><hr><center><i>";
    }
}
