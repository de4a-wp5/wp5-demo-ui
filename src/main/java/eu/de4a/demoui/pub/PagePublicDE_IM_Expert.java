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
package eu.de4a.demoui.pub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.url.URLHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.jquery.JQuery;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAnonymousFunction;
import com.helger.html.jscode.JSPackage;
import com.helger.html.jscode.JSVar;
import com.helger.html.jscode.html.JSHtml;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.HttpClientSettings;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.photon.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonGroup;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.grid.BootstrapGridSpec;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import eu.de4a.demoui.model.EDemoDocument;
import eu.de4a.demoui.model.EPatternType;
import eu.de4a.demoui.ui.AppCommonUI;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;

public class PagePublicDE_IM_Expert extends AbstractPageDE
{
  // We're doing a DR-IM request
  public static final EDemoDocument DEMO_DOC_TYPE = EDemoDocument.DR_IM_REQ;

  private static final String FIELD_TARGET_URL = "targeturl";
  private static final String FIELD_PAYLOAD = "payload";

  private static final AjaxFunctionDeclaration CREATE_NEW_REQUEST;

  @Nonnull
  private static RequestTransferEvidenceUSIIMDRType _createDemoRequest ()
  {
    RequestTransferEvidenceUSIIMDRType aDemoRequest;
    if (ThreadLocalRandom.current ().nextBoolean ())
    {
      while (true)
      {
        aDemoRequest = (RequestTransferEvidenceUSIIMDRType) DEMO_DOC_TYPE.createDemoRequest ();
        if (aDemoRequest.getDataRequestSubject ().getDataSubjectPerson () != null)
          break;
      }
      aDemoRequest.getDataOwner ().setAgentUrn (EMockDataOwner.PT.getID ());
      aDemoRequest.getDataRequestSubject ().getDataSubjectPerson ().setPersonIdentifier ("PT/NL/123456789");
      aDemoRequest.setCanonicalEvidenceTypeId (EUseCase.HIGHER_EDUCATION_DIPLOMA.getDocumentTypeID ().getURIEncoded ());
    }
    else
    {
      while (true)
      {
        aDemoRequest = (RequestTransferEvidenceUSIIMDRType) DEMO_DOC_TYPE.createDemoRequest ();
        if (aDemoRequest.getDataRequestSubject ().getDataSubjectCompany () != null)
          break;
      }
      aDemoRequest.getDataOwner ().setAgentUrn (EMockDataOwner.AT.getID ());
      aDemoRequest.getDataRequestSubject ().getDataSubjectCompany ().setLegalPersonIdentifier ("AT/NL/???");
      aDemoRequest.setCanonicalEvidenceTypeId (EUseCase.COMPANY_REGISTRATION.getDocumentTypeID ().getURIEncoded ());
    }
    return aDemoRequest;
  }

  static
  {
    CREATE_NEW_REQUEST = addAjax ( (aRequestScope, aAjaxResponse) -> {
      aAjaxResponse.text (DEMO_DOC_TYPE.getAnyMessageAsString (_createDemoRequest ()));
    });
  }

  public PagePublicDE_IM_Expert (@Nonnull @Nonempty final String sID)
  {
    super (sID, "IM Exchange (Expert)", EPatternType.IM);
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();

    final FormErrorList aFormErrors = new FormErrorList ();
    boolean bShowForm = true;
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sTargetURL = aWPEC.params ().getAsStringTrimmed (FIELD_TARGET_URL);
      final String sPayload = aWPEC.params ().getAsStringTrimmed (FIELD_PAYLOAD);

      if (StringHelper.hasNoText (sTargetURL))
        aFormErrors.addFieldError (FIELD_TARGET_URL, "A target URL is required");
      else
        if (URLHelper.getAsURL (sTargetURL, false) == null)
          aFormErrors.addFieldError (FIELD_TARGET_URL, "The target URL must be valid URL");

      if (StringHelper.hasNoText (sPayload))
        aFormErrors.addFieldError (FIELD_PAYLOAD, "Payload must be provided");

      if (aFormErrors.isEmpty ())
      {
        final HCNodeList aResNL = new HCNodeList ();

        // Check if document is valid
        final IErrorList aEL = DEMO_DOC_TYPE.validateMessage (sPayload);
        if (aEL.containsAtLeastOneError ())
        {
          aResNL.addChild (error ("The provided document is not XSD compliant"));
          for (final IError e : aEL)
            if (e.getErrorLevel ().isError ())
              aResNL.addChild (error (e.getAsString (aDisplayLocale)));
            else
              aResNL.addChild (warn (e.getAsString (aDisplayLocale)));
        }
        else
        {
          // Send only valid documents
          final RequestTransferEvidenceUSIIMDRType aParsedRequest = (RequestTransferEvidenceUSIIMDRType) DEMO_DOC_TYPE.parseMessage (sPayload);

          DE4AKafkaClient.send (EErrorLevel.INFO,
                                "DemoUI sending IM request '" + aParsedRequest.getRequestId () + "' to '" + sTargetURL + "'");

          final StopWatch aSW = StopWatch.createdStarted ();
          final HttpClientSettings aHCS = new HttpClientSettings ();
          aHCS.setConnectionRequestTimeoutMS (120_000);
          aHCS.setSocketTimeoutMS (120_000);
          try (final HttpClientManager aHCM = HttpClientManager.create (aHCS))
          {
            final HttpPost aPost = new HttpPost (sTargetURL);
            aPost.setEntity (new StringEntity (sPayload, ContentType.APPLICATION_XML.withCharset (StandardCharsets.UTF_8)));
            final byte [] aResponse = aHCM.execute (aPost, new ResponseHandlerByteArray ());
            DE4AKafkaClient.send (EErrorLevel.INFO, "Response content received (" + aResponse.length + " bytes)");
            final ResponseTransferEvidenceType aResponseObj = DE4AMarshaller.drImResponseMarshaller (IDE4ACanonicalEvidenceType.NONE)
                                                                            .read (aResponse);
            if (aResponseObj == null)
              throw new IOException ("Failed to parse response XML");
            if (aResponseObj.getErrorList () == null)
            {
              aResNL.addChild (h2 ("Preview of the response data"));
              aResNL.addChild (_createPreview (aWPEC, aResponseObj));
              final BootstrapButtonGroup aDiv = aResNL.addAndReturnChild (new BootstrapButtonGroup ());
              aDiv.addChild (new BootstrapButton (EBootstrapButtonType.SUCCESS).addChild ("Accept data")
                                                                               .setIcon (EDefaultIcon.YES)
                                                                               .setOnClick (JSHtml.windowAlert ("Okay, you accepted")));
              aDiv.addChild (new BootstrapButton (EBootstrapButtonType.OUTLINE_DANGER).addChild ("Reject data")
                                                                                      .setIcon (EDefaultIcon.NO)
                                                                                      .setOnClick (JSHtml.windowAlert ("Okay, you rejected")));
              bShowForm = false;
            }
            else
            {
              final HCUL aUL = new HCUL ();
              aResponseObj.getErrorList ().getError ().forEach (x -> aUL.addItem ("[" + x.getCode () + "] " + x.getText ()));
              aResNL.addChild (error (div ("The data could not be fetched from the Data Owner")).addChild (aUL));
            }
          }
          catch (final IOException ex)
          {
            aResNL.addChild (error ().addChild (div ("Error sending request to ").addChild (code (sTargetURL)))
                                     .addChild (AppCommonUI.getTechnicalDetailsUI (ex, true)));
          }
          finally
          {
            aSW.stop ();
            aResNL.addChild (info ("It took " + aSW.getMillis () + " milliseconds to get the result"));
          }
        }
        aNodeList.addChild (aResNL);
      }
    }

    if (bShowForm)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (aWPEC));
      aForm.setSplitting (BootstrapGridSpec.create (-1, -1, 2, 2, 2), BootstrapGridSpec.create (-1, -1, 10, 10, 10));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Target URL")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_TARGET_URL, TARGET_URL_MOCK_DO)))
                                                   .setHelpText (span ("The URL to send the request to. Use ").addChild (code (TARGET_URL_MOCK_DO))
                                                                                                              .addChild (" for the mock DO, or ")
                                                                                                              .addChild (code (TARGET_URL_TEST_DR))
                                                                                                              .addChild (" for the test DE4A Connector"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TARGET_URL)));
      {
        final HCTextArea aTA = new HCTextArea (new RequestField (FIELD_PAYLOAD,
                                                                 DEMO_DOC_TYPE.getAnyMessageAsString (_createDemoRequest ()))).setRows (10)
                                                                                                                              .addClass (CBootstrapCSS.TEXT_MONOSPACE);
        final JSAnonymousFunction aJSAppend = new JSAnonymousFunction ();
        final JSVar aJSAppendData = aJSAppend.param ("data");
        aJSAppend.body ().add (JQuery.idRef (aTA).val (aJSAppendData));

        final JSPackage aOnClick = new JSPackage ();
        aOnClick.add (new JQueryAjaxBuilder ().url (CREATE_NEW_REQUEST.getInvocationURL (aRequestScope)).success (aJSAppend).build ());
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Payload")
                                                     .setCtrl (aTA,
                                                               new BootstrapButton ().addChild ("Other message")
                                                                                     .setIcon (EDefaultIcon.REFRESH)
                                                                                     .setOnClick (aOnClick))
                                                     .setHelpText ("The message you want to send. By default a randomly generated message is created")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PAYLOAD)));
      }

      aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
      aForm.addChild (new BootstrapSubmitButton ().setIcon (EDefaultIcon.YES).addChild ("Send IM request"));
    }
  }
}
