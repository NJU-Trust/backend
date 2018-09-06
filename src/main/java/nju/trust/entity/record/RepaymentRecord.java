package nju.trust.entity.record;

import nju.trust.entity.target.BaseTarget;
import nju.trust.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class RepaymentRecord extends BaseRecord {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private BaseTarget target;

    private Double sum;

    private Double principal;

    private Double interest;

    private Double remainingPrincipal;

    private boolean payOff;

    @NotNull
    private LocalDate returnDate;

    private LocalDate actualRepayDate;

    private Integer period;

    public RepaymentRecord(User user, BaseTarget target, Double sum, Double principal, Integer period,
                           Double interest, Double remainingPrincipal, LocalDate returnDate) {
        super(user);
        this.target = target;
        this.sum = sum;
        this.principal = principal;
        this.period = period;
        this.interest = interest;
        this.remainingPrincipal = remainingPrincipal;
        this.payOff = false;
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        return overdueAndNotRepaid() || overdueButRepaid();
    }

    public boolean isBeforeSettlementDay() {
        return LocalDate.now().isBefore(returnDate);
    }

    public boolean isAtSettlementDay() {
        return LocalDate.now().isEqual(returnDate);
    }

    public long getOverdueDays() {
        if (overdueAndNotRepaid())
            return returnDate.until(LocalDate.now(), ChronoUnit.DAYS);
        else if (overdueButRepaid())
            return returnDate.until(actualRepayDate, ChronoUnit.DAYS);
        else
            return 0L;
    }

    public void makeRepaid() {
        payOff = true;
        actualRepayDate = LocalDate.now();
    }

    public Integer getPeriod() {
        return period;
    }

    public boolean isPayOff() {
        return payOff;
    }

    public void setPayOff(boolean payOff) {
        this.payOff = payOff;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaseTarget getTarget() {
        return target;
    }

    public void setTarget(BaseTarget target) {
        this.target = target;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(Double remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    private boolean overdueAndNotRepaid() {
        return !payOff && LocalDate.now().isAfter(returnDate);
    }

    private boolean overdueButRepaid() {
        return actualRepayDate.isAfter(returnDate);
    }
}
