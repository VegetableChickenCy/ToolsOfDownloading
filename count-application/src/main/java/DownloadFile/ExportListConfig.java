package DownloadFile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ExportListConfig {

    private List<List<String>> listList;

    @Bean
    public List<List<String>> getListList(){
        if (null == listList) {
            listList = new ArrayList<>();
        }
        return listList;
    }

}
