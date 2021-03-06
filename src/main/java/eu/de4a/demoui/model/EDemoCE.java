/**
 * Copyright (C) 2021 DE4A
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.demoui.model;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Element;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.math.MathHelper;

import eu.de4a.iem.jaxb.t41.uc1.v2021_02_11.ModeOfStudy;
import eu.de4a.iem.jaxb.t43.birth.v1_6.BirthType;
import eu.de4a.iem.jaxb.t43.birth.v1_6.ChildType;
import eu.de4a.iem.jaxb.t43.birth.v1_6.DateObjectType;
import eu.de4a.iem.jaxb.t43.domreg.v1_6.DomicileType;
import eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriageType;
import eu.de4a.iem.jaxb.w3.cv10.ac.CvaddressType;
import eu.de4a.iem.jaxb.w3.cv10.ac.CvidentifierType;
import eu.de4a.iem.jaxb.w3.cv10.bc.AdminunitFirstlineType;
import eu.de4a.iem.jaxb.w3.cv10.bc.AdminunitSecondlineType;
import eu.de4a.iem.jaxb.w3.cv10.bc.GivenNameType;
import eu.de4a.iem.jaxb.w3.cv10.bc.IdentifierType;
import eu.de4a.iem.jaxb.w3.cv10.bc.PostCodeType;
import eu.de4a.iem.jaxb.w3.cv11.bc.LegalEntityLegalNameType;
import eu.de4a.iem.xml.XSDDataTypeHelper;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.europa.data.europass.model.credentials_.IscedFOetCodeType;
import eu.europa.data.europass.model.credentials_.LanguageCharCodeEnumType;
import eu.europa.data.europass.model.credentials_.LanguageStringType;
import eu.europa.data.europass.model.credentials_.LegalIdentifierType;
import eu.europa.data.europass.model.credentials_.LocationType;
import eu.europa.data.europass.model.credentials_.PersonType;
import eu.europa.data.europass.model.credentials_.TextContentTypeCodeEnumType;
import oasis.names.specification.bdndr.schema.xsd.unqualifieddatatypes_1.CodeType;
import oasis.names.specification.bdndr.schema.xsd.unqualifieddatatypes_1.NumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_23.DateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_23.FamilyNameType;

/**
 * Demo canonical evidence type
 *
 * @author Philip Helger
 */
public enum EDemoCE
{
  T41_UC1_2021_02_11 (EDE4ACanonicalEvidenceType.T41_UC1_2021_02_11, EDemoCE::createT41_UC1_v2021_02_11),
  T41_UC1_2021_04_13 (EDE4ACanonicalEvidenceType.T41_UC1_2021_04_13, EDemoCE::createT41_UC1_v2021_04_13),
  T42_COMPANY_INFO_V06 (EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06, EDemoCE::createDBA_v06),
  T43_BIRTH_EVIDENCE_V16 (EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V16, EDemoCE::createMA_Birth_v16),
  T43_DOMREG_EVIDENCE_V16 (EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V16, EDemoCE::createMA_DomesticRegistration_v16),
  T43_MARRIAGE_EVIDENCE_V16 (EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V16, EDemoCE::createMA_Marriage_v16);

  private final EDE4ACanonicalEvidenceType m_eCEType;
  private final Supplier <Element> m_aAnyCreator;

  EDemoCE (@Nonnull final EDE4ACanonicalEvidenceType eCEType, final Supplier <Element> aAnyCreator)
  {
    m_eCEType = eCEType;
    m_aAnyCreator = aAnyCreator;
  }

  @Nonnull
  public EDE4ACanonicalEvidenceType getCEType ()
  {
    return m_eCEType;
  }

  @Nonnull
  public Element createExampleElement ()
  {
    return m_aAnyCreator.get ();
  }

  @Nonnull
  private static eu.europa.data.europass.model.credentials_.TextType _createText ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.europa.data.europass.model.credentials_.TextType t = new eu.europa.data.europass.model.credentials_.TextType ();
    t.setValue ("Text-" + MathHelper.abs (aTLR.nextInt ()));
    t.setContentType (EDemoDocument.random (TextContentTypeCodeEnumType.values ()));
    t.setLang (EDemoDocument.random (LanguageCharCodeEnumType.values ()));
    return t;
  }

  @Nonnull
  public static Element createT41_UC1_v2021_02_11 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t41.uc1.v2021_02_11.HigherEducationEvidenceType p = new eu.de4a.iem.jaxb.t41.uc1.v2021_02_11.HigherEducationEvidenceType ();
    p.setId ("urn:credential:" + UUID.randomUUID ().toString ());
    {
      final LanguageStringType a = new LanguageStringType ();
      a.setText (_createText ());
      p.addTitle (a);
    }
    p.addDegree (_createText ());
    p.setCountry ("http://publications.europa.eu/resource/authority/country/NZL");
    p.addInstitutionName (_createText ());
    p.addStudyProgramme (_createText ());
    p.setMainFieldOfStudy ("http://data.europa.eu/snb/isced-f/0610");
    p.setModeOfStudy (EDemoDocument.random (ModeOfStudy.values ()));
    p.setDurationOfEducation (new NumericType (MathHelper.toBigDecimal (MathHelper.abs (aTLR.nextInt ()))));
    p.setScope (new NumericType (MathHelper.toBigDecimal (MathHelper.abs (aTLR.nextInt ()))));
    p.setDateOfIssue (new DateType (PDTFactory.getCurrentLocalDate ()));
    p.addPlaceOfIssue (_createText ());
    {
      final PersonType a = new PersonType ();
      a.setId ("id-" + MathHelper.abs (aTLR.nextInt ()));
      {
        final LegalIdentifierType b = new LegalIdentifierType ();
        b.setValue ("NationalID-" + MathHelper.abs (aTLR.nextInt ()));
        b.setSpatialID ("SpatialID-" + MathHelper.abs (aTLR.nextInt ()));
        a.setNationalId (b);
      }
      {
        final LanguageStringType b = new LanguageStringType ();
        b.setText (_createText ());
        a.setGivenNames (b);
      }
      {
        final LanguageStringType b = new LanguageStringType ();
        b.setText (_createText ());
        a.setFamilyName (b);
      }
      a.setDateOfBirth (PDTFactory.getCurrentLocalDate ().minusYears (18 + aTLR.nextInt (80)));
      p.setHolderOfAchievement (a);
    }

    return eu.de4a.iem.xml.de4a.t41.v2021_02_11.DE4AT41Marshaller.higherEducationEvidence ().getAsDocument (p).getDocumentElement ();
  }

  @Nonnull
  public static Element createT41_UC1_v2021_04_13 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t41.uc1.v2021_04_13.HigherEducationDiplomaType p = new eu.de4a.iem.jaxb.t41.uc1.v2021_04_13.HigherEducationDiplomaType ();
    p.setId ("urn:credential:" + UUID.randomUUID ().toString ());
    {
      final LanguageStringType a = new LanguageStringType ();
      a.setText (_createText ());
      p.addTitle (a);
    }
    p.addDegree (_createText ());
    p.setCountry ("http://publications.europa.eu/resource/authority/country/NZL");
    p.addInstitutionName (_createText ());
    p.addStudyProgramme (_createText ());
    {
      final IscedFOetCodeType a = new IscedFOetCodeType ();
      a.setUri ("http://data.europa.eu/snb/isced-f/0610");
      p.setMainFieldOfStudy (a);
    }
    p.setModeOfStudy ("http://data.europa.eu/europass/learningScheduleType/fullTime");
    p.addDurationOfEducation (XSDDataTypeHelper.getFactory ()
                                               .newDurationYearMonth (true,
                                                                      MathHelper.abs (aTLR.nextInt (10)),
                                                                      MathHelper.abs (aTLR.nextInt (12))));
    p.setScope (MathHelper.toBigDecimal (MathHelper.abs (aTLR.nextInt ())));
    p.setDateOfIssue (new DateType (PDTFactory.getCurrentLocalDate ()));
    {
      final LocationType l = new LocationType ();
      {
        final LanguageStringType b = new LanguageStringType ();
        b.setText (_createText ());
        l.addName (b);
      }
      p.setPlaceOfIssue (l);
    }
    {
      final PersonType a = new PersonType ();
      a.setId ("id-" + MathHelper.abs (aTLR.nextInt ()));
      {
        final LegalIdentifierType b = new LegalIdentifierType ();
        b.setValue ("NationalID-" + MathHelper.abs (aTLR.nextInt ()));
        b.setSpatialID ("SpatialID-" + MathHelper.abs (aTLR.nextInt ()));
        a.setNationalId (b);
      }
      {
        final LanguageStringType b = new LanguageStringType ();
        b.setText (_createText ());
        a.setGivenNames (b);
      }
      {
        final LanguageStringType b = new LanguageStringType ();
        b.setText (_createText ());
        a.setFamilyName (b);
      }
      a.setDateOfBirth (PDTFactory.getCurrentLocalDate ().minusYears (18 + aTLR.nextInt (80)));
      p.setHolderOfAchievement (a);
    }

    return eu.de4a.iem.xml.de4a.t41.v2021_04_13.DE4AT41Marshaller.higherEducationDiploma ().getAsDocument (p).getDocumentElement ();
  }

  @Nonnull
  public static Element createDBA_v06 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t42.v0_6.LegalEntityType p = new eu.de4a.iem.jaxb.t42.v0_6.LegalEntityType ();
    {
      final eu.de4a.iem.jaxb.t42.v0_6.NamesType a = new eu.de4a.iem.jaxb.t42.v0_6.NamesType ();
      final LegalEntityLegalNameType aLegalName = new LegalEntityLegalNameType ();
      aLegalName.setValue ("LegalEntity-" + MathHelper.abs (aTLR.nextInt ()));
      a.setLegalEntityLegalName (aLegalName);
      p.addCompanyName (a);
    }
    p.setCompanyType ("CompanyType-" + MathHelper.abs (aTLR.nextInt ()));
    p.setCompanyStatus ("CompanyStatus-" + MathHelper.abs (aTLR.nextInt ()));
    {
      final eu.de4a.iem.jaxb.t42.v0_6.ActivityType a = new eu.de4a.iem.jaxb.t42.v0_6.ActivityType ();
      a.addNaceCode ("Nace-" + MathHelper.abs (aTLR.nextInt ()));
      p.setCompanyActivity (a);
    }
    p.setRegistrationDate (PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextInt (100)));
    p.setCompanyEUID ("CompanyEUID-" + MathHelper.abs (aTLR.nextInt ()));
    {
      final eu.de4a.iem.jaxb.t42.v0_6.AddressType a = new eu.de4a.iem.jaxb.t42.v0_6.AddressType ();
      a.setPoBox ("POBox-" + MathHelper.abs (aTLR.nextInt ()));
      p.addRegisteredAddress (a);
    }
    return eu.de4a.iem.xml.de4a.t42.v0_6.DE4AT42Marshaller.legalEntity ().getAsDocument (p).getDocumentElement ();
  }

  @Nonnull
  private static IdentifierType _createID ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final IdentifierType ret = new IdentifierType ();
    ret.setValue ("ID-" + MathHelper.abs (aTLR.nextInt ()));
    return ret;
  }

  @Nonnull
  private static CvidentifierType _createCvID ()
  {
    final CvidentifierType ret = new CvidentifierType ();
    ret.setIdentifier (_createID ());
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.birth.v1_6.PublicOrganisationType _createBirthPubOrg ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.birth.v1_6.PublicOrganisationType ret = new eu.de4a.iem.jaxb.t43.birth.v1_6.PublicOrganisationType ();
    ret.addIdentifier (_createCvID ());
    ret.addPrefLabel (new oasis.names.specification.bdndr.schema.xsd.unqualifieddatatypes_1.TextType ("PrefLabel-" +
                                                                                                      MathHelper.abs (aTLR.nextInt ())));
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.domreg.v1_6.PublicOrganisationType _createDomRegPubOrg ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.domreg.v1_6.PublicOrganisationType ret = new eu.de4a.iem.jaxb.t43.domreg.v1_6.PublicOrganisationType ();
    ret.addIdentifier (_createCvID ());
    ret.addPrefLabel (new oasis.names.specification.bdndr.schema.xsd.unqualifieddatatypes_1.TextType ("PrefLabel-" +
                                                                                                      MathHelper.abs (aTLR.nextInt ())));
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.marriage.v1_6.PublicOrganisationType _createMarriagePubOrg ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.marriage.v1_6.PublicOrganisationType ret = new eu.de4a.iem.jaxb.t43.marriage.v1_6.PublicOrganisationType ();
    ret.addIdentifier (_createCvID ());
    ret.addPrefLabel (new oasis.names.specification.bdndr.schema.xsd.unqualifieddatatypes_1.TextType ("PrefLabel-" +
                                                                                                      MathHelper.abs (aTLR.nextInt ())));
    return ret;
  }

  private static void _fill (@Nonnull final CvaddressType aAddr)
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    {
      final PostCodeType a = new PostCodeType ();
      a.setValue ("PostCode-" + MathHelper.abs (aTLR.nextInt ()));
      aAddr.addPostCode (a);
    }
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.birth.v1_6.LocationAddressType _createBirthLA ()
  {
    final eu.de4a.iem.jaxb.t43.birth.v1_6.LocationAddressType ret = new eu.de4a.iem.jaxb.t43.birth.v1_6.LocationAddressType ();
    {
      final AdminunitSecondlineType a = new AdminunitSecondlineType ();
      a.setValue ("L2");
      ret.setAdminUnitL2 (a);
    }
    {
      final AdminunitFirstlineType a = new AdminunitFirstlineType ();
      a.setValue ("L1");
      ret.setAdminUnitL1 (a);
    }
    _fill (ret);
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.birth.v1_6.ConstrainedLocationAddressType _createBirthCLA ()
  {
    final eu.de4a.iem.jaxb.t43.birth.v1_6.ConstrainedLocationAddressType ret = new eu.de4a.iem.jaxb.t43.birth.v1_6.ConstrainedLocationAddressType ();
    {
      final AdminunitSecondlineType a = new AdminunitSecondlineType ();
      a.setValue ("L2");
      ret.setAdminUnitL2 (a);
    }
    {
      final AdminunitFirstlineType a = new AdminunitFirstlineType ();
      a.setValue ("L1");
      ret.setAdminUnitL1 (a);
    }
    _fill (ret);
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.domreg.v1_6.ConstrainedLocationAddressType _createDomRegCLA ()
  {
    final eu.de4a.iem.jaxb.t43.domreg.v1_6.ConstrainedLocationAddressType ret = new eu.de4a.iem.jaxb.t43.domreg.v1_6.ConstrainedLocationAddressType ();
    {
      final AdminunitSecondlineType a = new AdminunitSecondlineType ();
      a.setValue ("L2");
      ret.setAdminUnitL2 (a);
    }
    {
      final AdminunitFirstlineType a = new AdminunitFirstlineType ();
      a.setValue ("L1");
      ret.setAdminUnitL1 (a);
    }
    _fill (ret);
    return ret;
  }

  @Nonnull
  private static eu.de4a.iem.jaxb.t43.marriage.v1_6.ConstrainedLocationAddressType _createMarriageCLA ()
  {
    final eu.de4a.iem.jaxb.t43.marriage.v1_6.ConstrainedLocationAddressType ret = new eu.de4a.iem.jaxb.t43.marriage.v1_6.ConstrainedLocationAddressType ();
    {
      final AdminunitSecondlineType a = new AdminunitSecondlineType ();
      a.setValue ("L2");
      ret.setAdminUnitL2 (a);
    }
    {
      final AdminunitFirstlineType a = new AdminunitFirstlineType ();
      a.setValue ("L1");
      ret.setAdminUnitL1 (a);
    }
    _fill (ret);
    return ret;
  }

  @Nonnull
  private static ChildType _createChildType ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final ChildType ret = new ChildType ();

    {
      final eu.de4a.iem.jaxb.t43.birth.v1_6.NameType a = new eu.de4a.iem.jaxb.t43.birth.v1_6.NameType ();
      {
        final GivenNameType b = new GivenNameType ();
        b.setValue ("Given-" + MathHelper.abs (aTLR.nextInt ()));
        a.addGivenName (b);
      }
      {
        final FamilyNameType b = new FamilyNameType ();
        b.setValue ("Family-" + MathHelper.abs (aTLR.nextInt ()));
        a.addFamilyName (b);
      }
      ret.setPersonName (a);
    }

    {
      final LocalDate aLD = PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextInt (2000));
      final DateObjectType a = new DateObjectType ();
      XMLGregorianCalendar c = XSDDataTypeHelper.getFactory ().newXMLGregorianCalendar ();
      c.setYear (aLD.getYear ());
      a.setYear (c);

      c = XSDDataTypeHelper.getFactory ().newXMLGregorianCalendar ();
      c.setMonth (aLD.getMonthValue ());
      a.setMonth (c);

      c = XSDDataTypeHelper.getFactory ().newXMLGregorianCalendar ();
      c.setDay (aLD.getDayOfMonth ());
      a.setDay (c);

      ret.setDateOfBirth (a);
    }
    ret.setGender (new CodeType (aTLR.nextBoolean () ? "m" : "f"));
    ret.setPlaceOfBirth (_createBirthLA ());
    return ret;
  }

  @Nonnull
  private static BirthType _createBirthType ()
  {
    final BirthType ret = new BirthType ();
    ret.setChild (_createChildType ());
    return ret;
  }

  @Nonnull
  public static Element createMA_Birth_v16 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.birth.v1_6.BirthEvidenceType p = new eu.de4a.iem.jaxb.t43.birth.v1_6.BirthEvidenceType ();
    p.setIdentifier (_createCvID ());
    p.setIssueDate (new DateType (PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextLong (2000))));
    p.setIssuingAuthority (_createBirthPubOrg ());
    p.setIssuingPlace (_createBirthCLA ());
    p.setCertifiesBirth (_createBirthType ());
    return eu.de4a.iem.xml.de4a.t43.v1_6.DE4AT43Marshaller.birthEvidence ().getAsDocument (p).getDocumentElement ();
  }

  @Nonnull
  private static DomicileType _createDomicileType ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final DomicileType ret = new DomicileType ();
    {
      final eu.de4a.iem.jaxb.t43.domreg.v1_6.PersonType a = new eu.de4a.iem.jaxb.t43.domreg.v1_6.PersonType ();
      {
        final eu.de4a.iem.jaxb.t43.domreg.v1_6.NameType b = new eu.de4a.iem.jaxb.t43.domreg.v1_6.NameType ();
        {
          final GivenNameType c = new GivenNameType ();
          c.setValue ("Given-" + MathHelper.abs (aTLR.nextInt ()));
          b.addGivenName (c);
        }
        {
          final FamilyNameType c = new FamilyNameType ();
          c.setValue ("Family-" + MathHelper.abs (aTLR.nextInt ()));
          b.addFamilyName (c);
        }
        a.setPersonName (b);
      }
      a.setGender (new CodeType (aTLR.nextBoolean () ? "m" : "f"));
      ret.setInhabitant (a);
    }
    {
      final eu.de4a.iem.jaxb.t43.domreg.v1_6.AddressType a = new eu.de4a.iem.jaxb.t43.domreg.v1_6.AddressType ();
      _fill (a);
      ret.setDomicile (a);
    }
    return ret;
  }

  @Nonnull
  public static Element createMA_DomesticRegistration_v16 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.domreg.v1_6.DomicileRegistrationEvidenceType p = new eu.de4a.iem.jaxb.t43.domreg.v1_6.DomicileRegistrationEvidenceType ();
    p.setIdentifier (_createCvID ());
    p.setIssueDate (new DateType (PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextLong (2000))));
    p.setIssuingAuthority (_createDomRegPubOrg ());
    p.setIssuingPlace (_createDomRegCLA ());
    p.setCertifiesDomicile (_createDomicileType ());
    return eu.de4a.iem.xml.de4a.t43.v1_6.DE4AT43Marshaller.domicileRegistrationEvidence ().getAsDocument (p).getDocumentElement ();
  }

  @Nonnull
  private static MarriageType _createMarriageType ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final MarriageType ret = new MarriageType ();
    ret.setDateOfMarriage (new DateType (PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextLong (2000))));

    {
      final eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriedPersonType a = new eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriedPersonType ();
      {
        final eu.de4a.iem.jaxb.t43.marriage.v1_6.NameType b = new eu.de4a.iem.jaxb.t43.marriage.v1_6.NameType ();
        {
          final GivenNameType c = new GivenNameType ();
          c.setValue ("Given-" + MathHelper.abs (aTLR.nextInt ()));
          b.addGivenName (c);
        }
        {
          final FamilyNameType c = new FamilyNameType ();
          c.setValue ("Family-" + MathHelper.abs (aTLR.nextInt ()));
          b.addFamilyName (c);
        }
        a.setPersonName (b);
      }
      a.setGender (new CodeType (aTLR.nextBoolean () ? "m" : "f"));
      ret.addSpouse (a);
    }
    {
      final eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriedPersonType a = new eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriedPersonType ();
      {
        final eu.de4a.iem.jaxb.t43.marriage.v1_6.NameType b = new eu.de4a.iem.jaxb.t43.marriage.v1_6.NameType ();
        {
          final GivenNameType c = new GivenNameType ();
          c.setValue ("Given-" + MathHelper.abs (aTLR.nextInt ()));
          b.addGivenName (c);
        }
        {
          final FamilyNameType c = new FamilyNameType ();
          c.setValue ("Family-" + MathHelper.abs (aTLR.nextInt ()));
          b.addFamilyName (c);
        }
        a.setPersonName (b);
      }
      a.setGender (new CodeType (aTLR.nextBoolean () ? "m" : "f"));
      ret.addSpouse (a);
    }
    return ret;
  }

  @Nonnull
  public static Element createMA_Marriage_v16 ()
  {
    final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    final eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriageEvidenceType p = new eu.de4a.iem.jaxb.t43.marriage.v1_6.MarriageEvidenceType ();
    p.setIdentifier (_createCvID ());
    p.setIssueDate (new DateType (PDTFactory.getCurrentLocalDate ().minusDays (aTLR.nextLong (2000))));
    p.setIssuingAuthority (_createMarriagePubOrg ());
    p.setIssuingPlace (_createMarriageCLA ());
    p.setCertifiesMarriage (_createMarriageType ());
    return eu.de4a.iem.xml.de4a.t43.v1_6.DE4AT43Marshaller.marriageEvidence ().getAsDocument (p).getDocumentElement ();
  }
}
