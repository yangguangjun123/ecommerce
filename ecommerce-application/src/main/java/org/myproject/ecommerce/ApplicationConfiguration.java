package org.myproject.ecommerce;

import org.bson.codecs.configuration.CodecProvider;
import org.myproject.ecommerce.core.codec.CustomCodecProvider;
import org.myproject.ecommerce.hvdfclient.HVDFCustomCodecProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApplicationConfiguration {

    @Bean(name = "codecProvider")
    public List<CodecProvider> codecProvider() {
        List<CodecProvider> codecProvider = new ArrayList<>();
        codecProvider.add(new CustomCodecProvider());
        codecProvider.add(new HVDFCustomCodecProvider());
        return codecProvider;
    }

}
