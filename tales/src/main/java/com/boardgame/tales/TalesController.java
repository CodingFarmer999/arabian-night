package com.boardgame.tales;

import com.boardgame.tales.model.EncounterResponse;
import com.boardgame.tales.model.Event;
import com.boardgame.tales.service.TalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TalesController {

    private final TalesService talesService;

    public TalesController(TalesService talesService) {
        this.talesService = talesService;
    }

    /**
     * 首頁：顯示查詢表單
     * 載入特殊遭遇卡列表供選單使用
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("specialCards", talesService.getSpecialEncounters());
        return "index";
    }

    /**
     * 處理遭遇查詢請求 (AJAX / HTMX)
     * 接收 EncounterID+Roll 或 SpecialEncounterID，回傳結果區塊
     */
    @PostMapping("/encounter")
    public String checkEncounter(
            @RequestParam(value = "encounterId", required = false) Integer encounterId,
            @RequestParam(value = "dieRoll", required = false) Integer dieRoll,
            @RequestParam(value = "specialEncounterId", required = false) Integer specialEncounterId,
            Model model) {

        // Validation: Need either (ID + Roll) OR (SpecialID)
        if (specialEncounterId == null && (encounterId == null || dieRoll == null)) {
            // Handle error or return empty
            return "index :: result-area";
        }

        EncounterResponse response = talesService.processEncounter(encounterId, dieRoll, specialEncounterId);
        model.addAttribute("response", response);
        return "index :: result-area";
    }

    /**
     * 處理行動選擇請求 (AJAX / HTMX)
     * 根據 矩陣 + 形容詞 + 行動，回傳最終故事內容
     */
    @PostMapping("/select-action")
    public String selectAction(@RequestParam("matrixLetter") String matrixLetter,
            @RequestParam("adjId") int adjId,
            @RequestParam("actionId") int actionId,
            Model model) {
        Event event = talesService.getEvent(matrixLetter, adjId, actionId);
        model.addAttribute("event", event);
        return "index :: story-display";
    }

    @GetMapping("/events")
    public String events() {
        return "events";
    }

    /**
     * 直接查詢事件 (Event ID)
     */
    @PostMapping("/event-lookup")
    public String lookupEvent(@RequestParam("eventId") int eventId, Model model) {
        Event event = talesService.getEventById(eventId);
        model.addAttribute("event", event);
        return "events :: story-display";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "<div id='result'>Hello from Spring Boot + HTMX! Time: " + java.time.LocalTime.now() + "</div>";
    }
}
