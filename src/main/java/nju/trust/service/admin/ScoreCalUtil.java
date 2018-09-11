package nju.trust.service.admin;

import nju.trust.dao.admin.UnstructuredDataRepository;
import nju.trust.dao.admin.UserEvidenceDao.UserEvidenceRepository;
import nju.trust.dao.admin.UserInfoCheckRecordRepository;
import nju.trust.dao.user.UserRepository;
import nju.trust.entity.CheckState;
import nju.trust.entity.UserLevel;
import nju.trust.entity.record.UserEvidence.*;
import nju.trust.entity.record.UserInfoCheckRecord;
import nju.trust.entity.user.UnstructuredData;
import nju.trust.entity.user.UnstructuredDataType;
import nju.trust.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 许杨
 * @Description: 个人管理
 * @Date: 2018/9/3
 */
@Service
public class ScoreCalUtil {
    private UserEvidenceRepository userEvidenceRepository;
    private UnstructuredDataRepository unstructuredDataRepository;
    private UserInfoCheckRecordRepository userInfoCheckRecordRepository;
    private UserRepository userRepository;
    @Autowired
    public ScoreCalUtil(UserEvidenceRepository userEvidenceRepository, UnstructuredDataRepository unstructuredDataRepository, UserInfoCheckRecordRepository userInfoCheckRecordRepository, UserRepository userRepository) {
        this.userEvidenceRepository = userEvidenceRepository;
        this.unstructuredDataRepository = unstructuredDataRepository;
        this.userInfoCheckRecordRepository = userInfoCheckRecordRepository;
        this.userRepository = userRepository;
    }

    // 计算得分并在后台更新
    public void calScore(UserInfoCheckRecord checkRecord) {
        // 修改状态为approve
        checkRecord.setCheckState(CheckState.PASS);
        userInfoCheckRecordRepository.save(checkRecord);

        switch (checkRecord.getCheckItem()) {
            case VOLUNTEERTIME:
                calVolunteerScore(checkRecord);
                break;
            case STUDENTWORK:
                calStudentWorkScore(checkRecord);
                break;
            case REWARD:
                calRewardScore(checkRecord);
                break;
            case MATCH:
                calMatchScore(checkRecord);
                break;
            case SCHOLARSHIP:
                calScholarshipScore(checkRecord);
                break;
            case SCHOOLTYPE:
                calSchoolTypeScore(checkRecord);
                break;
            case MAJOR:
                break;
            case EDUCATION:
                calEducationScore(checkRecord);
                break;
            case FAILNUM:
                calFailNumScore(checkRecord);
                break;
            case STUDY:
                calStudyScore(checkRecord);
                break;
            case DISCREDIT:
                calDiscreditScore(checkRecord);
                break;
            case TESTCHEAT:
                calTestCheatScore(checkRecord);
                break;
            case PAYMENT:
                calPaymentScore(checkRecord);
                break;
            case REPAYMENT:
                calRepaymentScore(checkRecord);
                break;
            case RETURNBOOKS:
                calReturnBooksScore(checkRecord);
                break;
        }

        changeBaseUserEvidenceState(checkRecord.getId(), CheckState.PASS);
    }
    // 计算每年平均志愿活动时长加分
    private void calVolunteerScore(UserInfoCheckRecord checkRecord) {
        Long id = checkRecord.getId();
        String username = checkRecord.getUser().getUsername();
        double time = userEvidenceRepository.getVolunteerTime(id);
        double score = time * 1.2;
        UnstructuredData pre = unstructuredDataRepository.findFirstByUserUsernameAndDataType(username, UnstructuredDataType.SOCIALITY);
        double preScore = pre.getScore();
        score = checkScore(preScore, score);
        pre.setScore(score);
        unstructuredDataRepository.save(pre);
    }
    // 学生工作
    private void calStudentWorkScore(UserInfoCheckRecord checkRecord) {
        Long id = checkRecord.getId();
        StudentWorkType workType = userEvidenceRepository.findStudentWorkEvidenceByItemId(id).getType();
        double score = workType.getScore();
        String username = checkRecord.getUser().getUsername();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.SOCIALITY);
        score = checkScore(preData.getScore(), score);
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 奖励
    private void calRewardScore(UserInfoCheckRecord checkRecord) {
        Long id = checkRecord.getId();
        String username = checkRecord.getUser().getUsername();
        RewardType type = userEvidenceRepository.findRewardEvidenceByItemId(id).getType();
        double score = type.getScore();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.AWARD);
        score = checkScore(preData.getScore(), score);
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 科研竞赛获奖情况
    private void calMatchScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        BonusType type = userEvidenceRepository.findMatchEvidenceByItemId(id).getType();
        double score = type.getScore();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.AWARD);
        score = checkScore(preData.getScore(), score);
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 奖学金
    private void calScholarshipScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        BonusType type = userEvidenceRepository.findScholarshipByItemId(id).getType();
        double score = type.getScore();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.AWARD);
        score = checkScore(preData.getScore(), score);
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 学校分类
    private void calSchoolTypeScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        SchoolType type = userEvidenceRepository.findSchoolByItemId(id).getSchoolType();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.SCHOOL);
        preData.setScore(type.getScore());
        unstructuredDataRepository.save(preData);
    }
    // 受教育情况
    private void calEducationScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        EducationType type = userEvidenceRepository.findEducationByItemId(id).getEducationType();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.EDUCATION);
        preData.setScore(type.getScore());
        unstructuredDataRepository.save(preData);
    }
    // 挂科数
    private void calFailNumScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findFailNumByItemId(id).getNum();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.FAILED_SUBJECTS);
        preData.setScore(calFailScore(num));
        unstructuredDataRepository.save(preData);
    }
    private double calFailScore(int num) {
        if(num == 0) {
            return 100;
        }else if(num <= 3) {
            return 60;
        }else if(num <= 5) {
            return 20;
        }else {
            return 0;
        }
    }
    // 学习成绩
    private void calStudyScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        double ranking = userEvidenceRepository.findStudyByItemId(id).getRanking();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.GRADE);
        preData.setScore(calStudyRankScore(ranking));
        unstructuredDataRepository.save(preData);
    }
    private double calStudyRankScore(double ranking) {
        return 100*(1-ranking);
    }
    // 是否为失信人员
    private void calDiscreditScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findDiscreditByItemId(id).getNum();
        if(num > 0) {
            // 为失信人员，无法再进行任何操作
            User user = userRepository.findByUsername(username).get();
            user.setUserLevel(UserLevel.DISCREDIT);
            userRepository.save(user);
        }
    }
    // 考试作弊
    private void calTestCheatScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findTestCheatByItemId(id).getNum();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.VIOLATION);
        double score = preData.getScore() - 100 * num;
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 学费及住宿费的缴纳情况
    private void calPaymentScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findPaymentByItemId(id).getNum();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.VIOLATION);
        double score = preData.getScore() - 50 * num;
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 贷款偿还
    private void calRepaymentScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findRepaymentByItemId(id).getNum();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.VIOLATION);
        double score = preData.getScore() - 100 * num;
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 图书馆借阅还书情况
    private void calReturnBooksScore(UserInfoCheckRecord checkRecord) {
        String username = checkRecord.getUser().getUsername();
        Long id = checkRecord.getId();
        int num = userEvidenceRepository.findReturnBooksByItemId(id).getNum();
        UnstructuredData preData = getUnstructuredData(username, UnstructuredDataType.VIOLATION);
        double score = preData.getScore() - 10 * num;
        preData.setScore(score);
        unstructuredDataRepository.save(preData);
    }
    // 检查分数 0 <= score <= 100
    private double checkScore(double preScore, double addScore) {
        double score = preScore + addScore;
        if(score > 100) {
            return 100;
        }else if(score < 0) {
            return 0;
        }else {
            return score;
        }
    }

    // 将baseUserEvidence状态修改为pass
    private void changeBaseUserEvidenceState(Long itemId, CheckState state) {
        List<BaseUserEvidence> evidences = userEvidenceRepository.findBasesByItemId(itemId);
        for(BaseUserEvidence evidence : evidences) {
            evidence.setState(state);
            userEvidenceRepository.save(evidence);
        }
    }
    // 获得非结构化数据
    public UnstructuredData getUnstructuredData(String username, UnstructuredDataType dataType) {
        // 检查unstructured_data是否存在，若无则初始化
        UnstructuredData data = unstructuredDataRepository.findFirstByUserUsernameAndDataType(username, dataType);
        if(data == null) {
            data = new UnstructuredData();
            data.setDataType(dataType);
            data.setScore(dataType.getInitialScore());
            User user = userRepository.findByUsername(username).get();
            data.setUser(user);
            unstructuredDataRepository.save(data);
            data = unstructuredDataRepository.findFirstByUserUsernameAndDataType(username, dataType);
        }
        return data;
    }

    public CheckState checkUserState(String username) {
        List<UserInfoCheckRecord> records = userInfoCheckRecordRepository.findByUserUsername(username);
        return null;
    }
}
