package nju.trust.web.personalinfo;

import nju.trust.payloads.personalinfomation.CampusPerformance;
import nju.trust.payloads.personalinfomation.InvestAndLoan;
import nju.trust.payloads.personalinfomation.PersonalDetailInfomation;
import nju.trust.payloads.personalinfomation.PersonalRelationship;
import nju.trust.service.personalinfo.PersonalInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: 许杨
 * @Description:
 * @Date: 2018/9/15
 */
@RestController
@RequestMapping("/profile")
public class PersonalInfoController {
    private PersonalInformationService personalInformationService;
    @Autowired
    public PersonalInfoController(PersonalInformationService personalInformationService) {
        this.personalInformationService = personalInformationService;
    }

    // 用户管理
    @GetMapping(value = "/campusPerformence")
    public CampusPerformance getCampusPerformance(String username) {
        return personalInformationService.getCampusPerformance(username);
    }

    // 信息表
    @GetMapping(value = "/information")
    public PersonalDetailInfomation getPersonalDetailInformation(String username) {
        return personalInformationService.getPersonalDetailInformation(username);
    }

    // 账户总览 投资借款部分
    @GetMapping(value = "/investAndLoan")
    public InvestAndLoan getInvestAndLoanInfo(String username) {
        return personalInformationService.getInvestAndLoanInfo(username);
    }

    // 校园关系图
    @GetMapping(value = "/relationship")
    public List<PersonalRelationship> getPersonalRelationships(String username) {
        return personalInformationService.getPersonalRelationships(username);
    }
}
