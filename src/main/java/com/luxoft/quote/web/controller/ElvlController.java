package com.luxoft.quote.web.controller;

import com.luxoft.quote.domain.Elvl;
import com.luxoft.quote.service.ElvlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElvlController {

    ElvlService elvlService;

    public ElvlController(ElvlService elvlService) {
        this.elvlService = elvlService;
    }

    @GetMapping("/elvls/{isin}")
    public Elvl getElvl(@PathVariable String isin) {
        return elvlService.getElvl(isin);
    }

    @GetMapping("/elvls")
    public List<Elvl> getElvls() {
        return elvlService.getElvls();
    }
}
