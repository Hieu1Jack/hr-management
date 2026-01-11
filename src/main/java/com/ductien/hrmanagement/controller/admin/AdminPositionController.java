package com.ductien.hrmanagement.controller.admin;

import com.ductien.hrmanagement.entity.Position;
import com.ductien.hrmanagement.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/positions")
public class AdminPositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping
    public String listPositions(Model model) {
        List<Position> positions = positionService.getAllPositions();
        model.addAttribute("positions", positions);
        return "admin/positions/index";
    }

    @GetMapping("/create")
    public String createPositionForm(Model model) {
        model.addAttribute("position", new Position());
        return "admin/positions/form";
    }

    @PostMapping("/create")
    public String createPosition(@ModelAttribute Position position) {
        positionService.createPosition(position);
        return "redirect:/admin/positions";
    }

    @GetMapping("/edit/{id}")
    public String editPositionForm(@PathVariable Integer id, Model model) {
        Position position = positionService.getPositionById(id).orElseThrow();
        model.addAttribute("position", position);
        return "admin/positions/form";
    }

    @PostMapping("/update")
    public String updatePosition(@ModelAttribute Position position) {
        positionService.updatePosition(position);
        return "redirect:/admin/positions";
    }

    @GetMapping("/delete/{id}")
    public String deletePosition(@PathVariable Integer id) {
        positionService.deletePosition(id);
        return "redirect:/admin/positions";
    }
}
