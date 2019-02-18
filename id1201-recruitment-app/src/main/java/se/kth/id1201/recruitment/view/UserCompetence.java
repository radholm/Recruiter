package se.kth.id1201.recruitment.view;

/**
 *  Object for storing a users competence
 * @author Perttu Jääskeläinen
 */
public class UserCompetence {
    private final Long competence;
    private final double experience;
    
    public UserCompetence(Long competenceID, double experience) {
        this.competence = competenceID;
        this.experience = experience;
    }
    public Long getCompetence() {
        return this.competence;
    }
    public double getExperience() {
        return (double)Math.round(this.experience * 100d) / 100d;
    }
}
