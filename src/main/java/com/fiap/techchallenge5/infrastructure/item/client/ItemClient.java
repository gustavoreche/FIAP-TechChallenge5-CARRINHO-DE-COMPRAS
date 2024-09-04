package com.fiap.techchallenge5.infrastructure.item.client;

import com.fiap.techchallenge5.infrastructure.item.client.response.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "item", url = "http://172.17.0.1:8081/item")
public interface ItemClient {

    @GetMapping(value = "/{ean}")
    ItemDTO pegaItem(@PathVariable(value = "ean") final Long ean,
                     @RequestHeader("Authorization") final String token);

}
