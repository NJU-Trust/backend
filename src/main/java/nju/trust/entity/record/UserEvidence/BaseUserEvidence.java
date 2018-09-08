package nju.trust.entity.record.UserEvidence;


import nju.trust.entity.CheckState;
import nju.trust.entity.record.UserInfoCheckRecord;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author: 许杨
 * @Description: 用户提交的条目的证明
 * @Date: 2018/9/1
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dataType", discriminatorType = DiscriminatorType.STRING)
public abstract class BaseUserEvidence {
    @Id
    @GeneratedValue
    private Long id;    // 编号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    private UserInfoCheckRecord item;  // 对应的条目

    private LocalDateTime time; // 申请时间

    @Enumerated(value = EnumType.STRING)
    private CheckState state;   // 审核结果

    /**
     * 审核的图片
     */
    @Lob
    private String evidence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfoCheckRecord getItem() {
        return item;
    }

    public void setItem(UserInfoCheckRecord item) {
        this.item = item;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public CheckState getState() {
        return state;
    }

    public void setState(CheckState state) {
        this.state = state;
    }

    public BaseUserEvidence(UserInfoCheckRecord item, LocalDateTime time, CheckState state, String evidence) {
        this.item = item;
        this.time = time;
        this.state = state;
        this.evidence = evidence;
    }
}