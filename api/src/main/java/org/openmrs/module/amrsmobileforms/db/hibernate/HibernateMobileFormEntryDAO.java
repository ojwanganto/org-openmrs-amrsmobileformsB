package org.openmrs.module.amrsmobileforms.db.hibernate;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO;

/**
 * Database interface for the module
 *
 * @author Samuel Mbugua
 */
public class HibernateMobileFormEntryDAO implements MobileFormEntryDAO {

	private static Log log = LogFactory.getLog(MobileFormEntryDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * Default public constructor
	 */
	public HibernateMobileFormEntryDAO() {
	}

	/**
	 * Set session factory
	 *
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getHousehold(java.lang.String)
	 */
	public MobileFormHousehold getHousehold(String householdIdentifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MobileFormHousehold.class);
		MobileFormHousehold household = (MobileFormHousehold) criteria.add(Expression.like("householdIdentifier", householdIdentifier)).uniqueResult();
		return household;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveHousehold(org.openmrs.module.amrsmobileforms.MobileFormHousehold)
	 */
	public void saveHousehold(MobileFormHousehold household) {
		sessionFactory.getCurrentSession().saveOrUpdate(household);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getEconomicObjectByObjectName(java.lang.String)
	 */
	public EconomicObject getEconomicObjectByObjectName(String objectName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		EconomicObject econObject = (EconomicObject) criteria.add(Expression.like("objectName", objectName)).uniqueResult();

		return econObject;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#createEconomicInDatabase(org.openmrs.module.amrsmobileforms.Economic)
	 */
	public void createEconomicInDatabase(Economic economic) {
		sessionFactory.getCurrentSession().saveOrUpdate(economic);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#createSurvey(org.openmrs.module.amrsmobileforms.Survey)
	 */
	public void createSurvey(Survey survey) {
		sessionFactory.getCurrentSession().saveOrUpdate(survey);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getAllEconomicObjects()
	 */
	@SuppressWarnings("unchecked")
	public List<EconomicObject> getAllEconomicObjects() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		return (List<EconomicObject>) criteria.list();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public void saveEconomicObject(EconomicObject economicObject) {
		sessionFactory.getCurrentSession().saveOrUpdate(economicObject);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#deleteEconomicObject(java.lang.Integer)
	 */
	public boolean deleteEconomicObject(EconomicObject economicObject) {
		sessionFactory.getCurrentSession().delete(economicObject);
		return true;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveErrorInDatabase(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError) {
		sessionFactory.getCurrentSession().saveOrUpdate(mobileFormEntryError);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getAllMobileFormEntryErrors()
	 */
	@SuppressWarnings("unchecked")
	public List<MobileFormEntryError> getAllMobileFormEntryErrors() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MobileFormEntryError.class);
		return (List<MobileFormEntryError>) criteria.list();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getErrorById(java.lang.Integer)
	 */
	public MobileFormEntryError getErrorById(Integer errorId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MobileFormEntryError.class);
		criteria.add(Expression.like("mobileFormEntryErrorId", errorId));
		return (MobileFormEntryError) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getHouseholdMemberById(java.lang.Integer)
	 */
	public HouseholdMember getHouseholdMemberById(Integer identifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HouseholdMember.class);
		criteria.add(Expression.eq("householdMemberId", identifier));
		return (HouseholdMember) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getEconomicObjectById(java.lang.Integer)
	 */
	public EconomicObject getEconomicObjectById(Integer economicObjectId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		criteria.add(Expression.like("objectId", economicObjectId));
		return (EconomicObject) criteria.uniqueResult();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#deleteError(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void deleteError(MobileFormEntryError error) {
		sessionFactory.getCurrentSession().delete(error);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveHouseholdMember(org.openmrs.module.amrsmobileforms.HouseholdMember)
	 */
	public void saveHouseholdMember(HouseholdMember householdMember) {
		sessionFactory.getCurrentSession().saveOrUpdate(householdMember);
	}

	@SuppressWarnings("unchecked")
	public List<HouseholdMember> getAllMembersInHousehold(MobileFormHousehold household) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HouseholdMember.class);
		criteria.add(Expression.eq("household", household));
		return (List<HouseholdMember>) criteria.list();
	}

	public EconomicConceptMap getEconomicConceptMapFor(EconomicObject eo) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicConceptMap.class);
		criteria.add(Expression.eq("economic", eo));
		return (EconomicConceptMap) criteria.uniqueResult();
	}

	public EconomicConceptMap getEconomicConceptMap(Integer id) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicConceptMap.class);
		criteria.add(Expression.eq("economicConceptMapId", id));
		return (EconomicConceptMap) criteria.uniqueResult();
	}

	public EconomicConceptMap saveEconomicConceptMap(EconomicConceptMap ecm) {
		sessionFactory.getCurrentSession().saveOrUpdate(ecm);
		return ecm;
	}

	private Criteria getErrorCriteria(String query) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(MobileFormEntryError.class);
		String idQuery = StringUtils.isNumeric(query) ? "%" + query + "%" : "";

		if (query != null && !query.isEmpty()) {
			Criterion disjunction = Restrictions.disjunction()
					.add(Restrictions.sqlRestriction("mobile_formentry_error_id like '" + idQuery + "'"))
                    .add(Restrictions.like("providerId", query, MatchMode.ANYWHERE))
                    .add(Restrictions.like("locationId", query, MatchMode.ANYWHERE))
					.add(Restrictions.like("error", query, MatchMode.ANYWHERE))
					.add(Restrictions.like("errorDetails", query, MatchMode.ANYWHERE))
					.add(Restrictions.like("formName", query, MatchMode.ANYWHERE));
			crit.add(disjunction);
		}

		return crit;
	}

	public List<MobileFormEntryError> getErrorBatch(Integer start, Integer length, String query) {
		Criteria crit = getErrorCriteria(query);

		crit.setFirstResult(start);
		if (length != null) {
			crit.setMaxResults(length);
		}
		crit.addOrder(Order.asc("dateCreated"));

		return crit.list();
	}

	public Number countErrors(String query) {
		Criteria crit = getErrorCriteria(query);
		crit.setProjection(Projections.rowCount());
		return (Number) crit.uniqueResult();
	}
}