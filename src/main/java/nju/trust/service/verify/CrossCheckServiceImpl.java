package nju.trust.service.verify;

import nju.trust.dao.user.UserCrossCheckRepository;
import nju.trust.dao.user.UserRepository;
import nju.trust.entity.user.CreditCrossCheck;
import nju.trust.entity.user.User;
import nju.trust.payloads.ApiResponse;
import nju.trust.payloads.verifyInfo.CrossCheckInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @Author: 161250127
 * @Description:
 * @Date: 2018/10/21
 */
@Service
public class CrossCheckServiceImpl implements CrossCheckService {

    private UserRepository userRepository;
    private UserCrossCheckRepository userCrossCheckRepository;

    public CrossCheckServiceImpl(UserRepository userRepository, UserCrossCheckRepository userCrossCheckRepository) {
        this.userRepository = userRepository;
        this.userCrossCheckRepository = userCrossCheckRepository;
    }

    @Override
    public ApiResponse setUpNetwork(String username, String studentId1, String studentId2, String studentId3) {
        if(!userRepository.existsByUsername(username)){
            return new ApiResponse(false,"username not exist!");
        }
        if(userRepository.existsByStudentId(studentId1)&&userRepository.existsByStudentId(studentId2)&&userRepository.existsByStudentId(studentId3)){
            //1.find users having the same institution
            User user = userRepository.findByUsername(username).get();
            List<User> users = userRepository.findAllByInstitution(user.getInstitution());
            //2.find random 7 person and set up network
            if(users.size()<11){
                return new ApiResponse(false,"the users are not enough to verify!");
            }else{
                LocalDate now = LocalDate.now();
                LocalDate endDate = now.plusDays(3);
                //choose 3
                userCrossCheckRepository.save(new CreditCrossCheck(user, userRepository.findByStudentId(studentId1).get(), endDate, false));
                userCrossCheckRepository.save(new CreditCrossCheck(user, userRepository.findByStudentId(studentId2).get(), endDate, false));
                userCrossCheckRepository.save(new CreditCrossCheck(user, userRepository.findByStudentId(studentId3).get(), endDate, false));
                //random 7
                Collections.shuffle(users);
                for(int i=0;i<=11;i++){
                    if(users.get(i).getUsername().equals(username)||users.get(i).getStudentId().equals(studentId1)
                            ||users.get(i).getStudentId().equals(studentId2)||users.get(i).getStudentId().equals(studentId3)){
                        continue;
                    }else{
                        userCrossCheckRepository.save(new CreditCrossCheck(user, users.get(i), endDate, true));
                    }
                }
            }
        }else {
            return new ApiResponse(false,"studentId not exist!");
        }
        return null;
    }

    @Override
    public List<CrossCheckInfo> getQuestionnaireList(String username) {
        List<CreditCrossCheck> creditCrossChecks = userCrossCheckRepository.findAllByRelatedPersonUsernameAndValid(username,true);
        List<CrossCheckInfo> crossCheckInfos = new ArrayList<>();
        for(int i=0;i<creditCrossChecks.size();i++){
            crossCheckInfos.add(new CrossCheckInfo(creditCrossChecks.get(i)));
        }
        return crossCheckInfos;
    }

    @Override
    public ApiResponse submitQuestionnaire(long id, int q1, int q2, int q3, int q4, int q5, int q6, int q7, int q8, int q9) {
        return null;
    }
}
