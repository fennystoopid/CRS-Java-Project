
package service;

import model.Student;

public interface EligibilityChecker {
    boolean isEligible(Student s);
}
// Interface used to define a RULE for eligibility.
// This allows the system to change rules easily in the future.
// Any class that implements this interface must define how eligibility is checked.
