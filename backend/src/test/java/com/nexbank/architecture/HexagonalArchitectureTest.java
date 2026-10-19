package com.nexbank.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    static JavaClasses classes;

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.nexbank");
    }

    @Test
    void domainShouldNotDependOnSpring() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.nexbank.domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..");
        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnJpa() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.nexbank.domain..")
            .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");
        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnAdapters() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.nexbank.domain..")
            .should().dependOnClassesThat().resideInAPackage("com.nexbank.adapters..");
        rule.check(classes);
    }

    @Test
    void applicationShouldNotDependOnAdapters() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.nexbank.application..")
            .should().dependOnClassesThat().resideInAPackage("com.nexbank.adapters..");
        rule.check(classes);
    }

    @Test
    void adaptersShouldNotDependOnOtherAdapters() {
        ArchRule rule = noClasses()
            .that().resideInAPackage("com.nexbank.adapters.in..")
            .should().dependOnClassesThat().resideInAPackage("com.nexbank.adapters.out..");
        rule.check(classes);
    }
}
