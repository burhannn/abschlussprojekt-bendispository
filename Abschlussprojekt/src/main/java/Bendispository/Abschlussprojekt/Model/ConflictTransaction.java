package Bendispository.Abschlussprojekt.Model;

import Bendispository.Abschlussprojekt.Repo.ConflictTransactionRepo;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ConflictTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  private boolean lenderAccepted;

  private boolean leaserAccepted;

  private int damageCosts;

  private int validationTime;

  public void addConflictTransaction() {
      ConflictTransaction cfTrans = new ConflictTransaction();
  }
}