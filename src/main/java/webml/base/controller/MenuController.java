package webml.base.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import webml.base.core.exception.MessageException;
import webml.base.core.security.MemberDetails;
import webml.base.dto.MenuDto;
import webml.base.service.AuthorityService;
import webml.base.service.MenuService;

import java.util.HashMap;
import java.util.Map;

/**
 * 메뉴관리
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    private final AuthorityService authorityService;

    /**
     * 메뉴 목록 조회(+권한목록)
     */
    @GetMapping
    public String getMenuList(Model model) throws Exception {
        //비동기 호출 아니므로 MessageException 사용불가
        try {
            MemberDetails principal = (MemberDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("menus", menuService.getMenuMngList(principal.getAuthoritiesStrArr()));
            model.addAttribute("authorities", authorityService.getAuthorities());
        } catch (Exception e) {
            log.error("", e);
            throw new Exception(e);
        }
        return "menu/menu_mng";
    }

    /**
     * 메뉴 정보 조회
     */
    @ResponseBody
    @GetMapping("/{menuIdx}")
    public Map<String, Object> getMenuInfo(@PathVariable Long menuIdx) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            rtnMap.put("menuInfo", menuService.getMenuInfo(menuIdx));
        } catch (Exception e) {
            log.error("", e);
            throw new MessageException(e.getMessage());
        }
        return rtnMap;
    }

    /**
     * 메뉴 저장
     */
    @ResponseBody
    @PostMapping
    public Map<String, Object> saveMenu(@RequestBody @Valid MenuDto menuDto, BindingResult bindingResult) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            if (bindingResult.hasErrors()) {
                FieldError error = bindingResult.getFieldErrors().get(0);
                throw new MessageException(error.getDefaultMessage());
            }

            menuService.saveMenu(menuDto);
        } catch (Exception e) {
            log.error("", e);
            throw new MessageException(e.getMessage());
        }

        rtnMap.put("message", "저장되었습니다.");
        return rtnMap;
    }

    /**
     * 메뉴 정보 수정
     */
    @ResponseBody
    @PostMapping("/{menuIdx}")
    public Map<String, Object> updateMenu(@PathVariable Long menuIdx, @RequestBody @Valid MenuDto menuDto, BindingResult bindingResult) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            if (bindingResult.hasErrors()) {
                FieldError error = bindingResult.getFieldErrors().get(0);
                throw new MessageException(error.getDefaultMessage());
            }
            menuDto.setMenuIdx(menuIdx);
            menuService.updateMenu(menuDto);
        } catch (Exception e) {
            log.error("", e);
            throw new MessageException(e.getMessage());
        }
        rtnMap.put("message", "저장되었습니다.");
        return rtnMap;
    }

    /**
     * 메뉴 삭제
     */
    @ResponseBody
    @DeleteMapping("/{menuIdx}")
    public Map<String, Object> deleteMenu(@PathVariable Long menuIdx) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            menuService.deleteMenu(menuIdx);
            rtnMap.put("message", "삭제되었습니다.");
        } catch (Exception e) {
            log.error("", e);
            throw new MessageException(e.getMessage());
        }
        return rtnMap;
    }

    /**
     * 메뉴 순서 업데이트
     */
    @ResponseBody
    @PostMapping("/setMenuOrder")
    public Map<String, Object> setMenuOrder(@RequestParam(name = "menuIdxOrder[]") Long[] menuIdxList) {
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            menuService.updateOrder(menuIdxList);
        } catch (Exception e) {
            log.error("", e);
            throw new MessageException(e.getMessage());
        }
        return rtnMap;
    }


}
