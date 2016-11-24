package com.idataitech.gdg.devfest2016.ml;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Html {

    @RequestMapping("/**/*.html")
    public void html() {

    }

}
