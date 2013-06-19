package org.openmrs.module.amrsmobileforms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MobileFormUploadProcessorTest {

	private MobileFormUploadProcessor processor = new MobileFormUploadProcessor();

	/**
	 * convenience method for easily creating a set of person names
	 *
	 * @param personNames
	 * @return
	 */
	private Set<PersonName> makePersonNameSet(PersonName... personNames) {
		Set<PersonName> results = new HashSet<PersonName>();
		for (PersonName personName : personNames) {
			results.add(personName);
		}
		return results;
	}

	/**
	 * @verifies return true when only one name part is found in a person name
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnTrueWhenOnlyOneNamePartIsFoundInAPersonName() throws Exception {
		List<String> nameParts = Arrays.asList("Lloyd");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Wright")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}

	/**
	 * @verifies return true when more than one name part matches
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnTrueWhenMoreThanOneNamePartMatches() throws Exception {
		List<String> nameParts = Arrays.asList("Lloyd");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Lloyd")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}

	/**
	 * @verifies return true when the found patient has more than one name
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnTrueWhenTheFoundPatientHasMoreThanOneName() throws Exception {
		List<String> nameParts = Arrays.asList("Lloyd");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Wright"),
				new PersonName("Franklin", "Delano", "Roosevelt")
				);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}

	/**
	 * @verifies return true when the matching name is not preferred
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnTrueWhenTheMatchingNameIsNotPreferred() throws Exception {
		List<String> nameParts = Arrays.asList("Lloyd");

		PersonName preferred = new PersonName("Franklin", "Delano", "Roosevelt");
		preferred.setPreferred(true);

		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Wright"),
				preferred
		);

		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}

	/**
	 * @verifies return false if no name part is found and there is only one name
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnFalseIfNoNamePartIsFoundAndThereIsOnlyOneName() throws Exception {
		List<String> nameParts = Arrays.asList("Lloyd");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Franklin", "Delano", "Roosevelt")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(false));
	}

	/**
	 * @verifies return false if no name part is found and there are multiple names
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnFalseIfNoNamePartIsFoundAndThereAreMultipleNames() throws Exception {
		List<String> nameParts = Arrays.asList("Alfayo");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Wright"),
				new PersonName("Franklin", "Delano", "Roosevelt")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(false));
	}

	/**
	 * @verifies return false if no providedNames are provided
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnFalseIfNoProvidedNamesAreProvided() throws Exception {
		List<String> nameParts = null;
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd", "Wright"),
				new PersonName("Franklin", "Delano", "Roosevelt")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(false));
	}

	/**
	 * @verifies return false if no personNames are provided
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldReturnFalseIfNoPersonNamesAreProvided() throws Exception {
		List<String> nameParts = Arrays.asList("Alfayo");
		Set<PersonName> personNameSet = null;
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(false));
	}

	/**
	 * @verifies match on name parts with additional spaces
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldMatchOnNamePartsWithAdditionalSpaces() throws Exception {
		List<String> nameParts = Arrays.asList("   Lloyd  ");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "Lloyd ", "Wright")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}

	/**
	 * @verifies match on name parts with different capitalization
	 * @see MobileFormUploadProcessor#matchesOnNameParts(java.util.List, java.util.Set)
	 */
	@Test
	public void matchesOnNameParts_shouldMatchOnNamePartsWithDifferentCapitalization() throws Exception {
		List<String> nameParts = Arrays.asList("LLOYD");
		Set<PersonName> personNameSet = makePersonNameSet(
				new PersonName("Frank", "lLoyD", "Wright")
		);
		assertThat(processor.matchesOnNameParts(nameParts, personNameSet), is(true));
	}
}
