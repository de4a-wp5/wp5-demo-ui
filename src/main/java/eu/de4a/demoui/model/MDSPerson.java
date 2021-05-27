package eu.de4a.demoui.model;

import java.time.LocalDate;
import java.time.Month;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.builder.IBuilder;
import com.helger.commons.datetime.PDTFactory;

/**
 * Minimum Data Set for a Person
 *
 * @author Philip Helger
 */
@Immutable
public class MDSPerson
{
  private final String m_sID;
  private final String m_sFirstName;
  private final String m_sFamilyName;
  private final LocalDate m_aBirthday;

  public MDSPerson (@Nonnull @Nonempty final String sID,
                    @Nonnull @Nonempty final String sFirstName,
                    @Nonnull @Nonempty final String sFamilyName,
                    @Nonnull final LocalDate aBirthday)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notEmpty (sFirstName, "FirstName");
    ValueEnforcer.notEmpty (sFamilyName, "FamilyName");
    ValueEnforcer.notNull (aBirthday, "Birthday");
    m_sID = sID;
    m_sFirstName = sFirstName;
    m_sFamilyName = sFamilyName;
    m_aBirthday = aBirthday;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getFirstName ()
  {
    return m_sFirstName;
  }

  @Nonnull
  @Nonempty
  public String getFamilyName ()
  {
    return m_sFamilyName;
  }

  @Nonnull
  public LocalDate getBirthday ()
  {
    return m_aBirthday;
  }

  @Nonnull
  public static MDSPerson.Builder builder ()
  {
    return new MDSPerson.Builder ();
  }

  public static class Builder implements IBuilder <MDSPerson>
  {
    private String m_sID;
    private String m_sFirstName;
    private String m_sFamilyName;
    private LocalDate m_aBirthday;

    public Builder ()
    {}

    @Nonnull
    public MDSPerson.Builder id (@Nullable final String s)
    {
      m_sID = s;
      return this;
    }

    @Nonnull
    public MDSPerson.Builder firstName (@Nullable final String s)
    {
      m_sFirstName = s;
      return this;
    }

    @Nonnull
    public MDSPerson.Builder familyName (@Nullable final String s)
    {
      m_sFamilyName = s;
      return this;
    }

    @Nonnull
    public MDSPerson.Builder birthday (final int y, final int m, final int d)
    {
      return birthday (PDTFactory.createLocalDate (y, Month.of (m), d));
    }

    @Nonnull
    public MDSPerson.Builder birthday (@Nullable final LocalDate a)
    {
      m_aBirthday = a;
      return this;
    }

    @Nonnull
    public MDSPerson build ()
    {
      return new MDSPerson (m_sID, m_sFirstName, m_sFamilyName, m_aBirthday);
    }
  }
}