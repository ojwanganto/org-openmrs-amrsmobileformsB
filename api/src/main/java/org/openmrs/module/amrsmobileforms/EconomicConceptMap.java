package org.openmrs.module.amrsmobileforms;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;

/**
 * Economic-to-Concept mapping class.  Maps Economic Objects to Concept questions and answers.
 */
public class EconomicConceptMap extends BaseOpenmrsMetadata {

	Integer economicConceptMapId;
	EconomicObject economic;
	Concept concept;

	public EconomicConceptMap() {
		// pass
	}
	
	public EconomicConceptMap(EconomicObject eo) {
		this.economic = eo;
	}
	
	public Integer getId() {
		return this.getEconomicConceptMapId();
	}

	public void setId(Integer id) {
		this.setEconomicConceptMapId(id);
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public EconomicObject getEconomic() {
		return economic;
	}

	public void setEconomic(EconomicObject economic) {
		this.economic = economic;
	}

	public Integer getEconomicConceptMapId() {
		return economicConceptMapId;
	}

	public void setEconomicConceptMapId(Integer economicConceptMapId) {
		this.economicConceptMapId = economicConceptMapId;
	}
	
}
