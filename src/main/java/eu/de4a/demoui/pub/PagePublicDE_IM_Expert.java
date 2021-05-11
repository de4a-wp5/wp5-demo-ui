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

import javax.annotation.Nonnull;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.jscode.html.JSHtml;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.HttpClientSettings;
import com.helger.httpclient.response.ResponseHandlerByteArray;
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

import eu.de4a.demoui.CApp;
import eu.de4a.demoui.ui.AppCommonUI;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.jaxb.common.types.ResponseTransferEvidenceType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;

public class PagePublicDE_IM_Expert extends AbstractPageDE4ARequest
{
  private static final String FIELD_PAYLOAD = "payload";

  public PagePublicDE_IM_Expert (@Nonnull @Nonempty final String sID)
  {
    super (sID, "IM Exchange (Expert)");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    // We're doing a DO-USI request
    final EDemoDocument eDemoDoc = EDemoDocument.DR_IM_REQ;
    final String sTargetURL = CApp.DEFAULT_BASE_URL + eDemoDoc.getRelativeURL ();

    final FormErrorList aFormErrors = new FormErrorList ();
    boolean bShowForm = true;
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sPayload = aWPEC.params ().getAsStringTrimmed (FIELD_PAYLOAD);
      if (StringHelper.hasNoText (sPayload))
        aFormErrors.addFieldError (FIELD_PAYLOAD, "Payload must be provided");

      if (aFormErrors.isEmpty ())
      {
        final HCNodeList aResNL = new HCNodeList ();

        // Check if document is valid
        final IErrorList aEL = eDemoDoc.validateMessage (sPayload);
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
          final RequestTransferEvidenceUSIIMDRType aParsedRequest = (RequestTransferEvidenceUSIIMDRType) eDemoDoc.parseMessage (sPayload);

          DE4AKafkaClient.send (EErrorLevel.INFO, "DemoUI sending IM request '" + aParsedRequest.getRequestId () + "'");

          final HttpClientSettings aHCS = new HttpClientSettings ();
          try (final HttpClientManager aHCM = HttpClientManager.create (aHCS))
          {
            final HttpPost aPost = new HttpPost (sTargetURL);
            aPost.setEntity (new StringEntity (sPayload,
                                               ContentType.APPLICATION_XML.withCharset (StandardCharsets.UTF_8)));
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
              aResNL.addChild (h2 ("The data could not be fetched from the Data Owner"));
              final HCUL aUL = new HCUL ();
              aResponseObj.getErrorList ()
                          .getError ()
                          .forEach (x -> aUL.addItem ("[" + x.getCode () + "] " + x.getText ()));
              aResNL.addChild (aUL);
            }
          }
          catch (final IOException ex)
          {
            aResNL.addChild (error ().addChild (div ("Error sending request to ").addChild (code (sTargetURL)))
                                     .addChild (AppCommonUI.getTechnicalDetailsUI (ex, true)));
          }
        }
        aNodeList.addChild (aResNL);
      }
    }

    if (bShowForm)
    {
      RequestTransferEvidenceUSIIMDRType aDemoRequest;
      while (true)
      {
        aDemoRequest = (RequestTransferEvidenceUSIIMDRType) eDemoDoc.createDemoRequest ();
        if (aDemoRequest.getDataRequestSubject ().getDataSubjectPerson () != null)
          break;
      }
      aDemoRequest.getDataOwner ().setAgentUrn ("iso6523-actorid-upis::9999:PT990000101");
      aDemoRequest.getDataRequestSubject ().getDataSubjectPerson ().setPersonIdentifier ("PT/NL/123456789");
      aDemoRequest.setCanonicalEvidenceTypeId ("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma");

      final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (aWPEC));
      aForm.setSplitting (BootstrapGridSpec.create (-1, -1, 2, 2, 2), BootstrapGridSpec.create (-1, -1, 10, 10, 10));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Target URL").setCtrl (code (sTargetURL)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Payload")
                                                   .setCtrl (new HCTextArea (new RequestField (FIELD_PAYLOAD,
                                                                                               eDemoDoc.getAnyMessageAsString (aDemoRequest))).setRows (10)
                                                                                                                                              .addClass (CBootstrapCSS.TEXT_MONOSPACE))
                                                   .setHelpText ("The message you want to send. By default a randomly generated message is created")
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_PAYLOAD)));

      aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
      aForm.addChild (new BootstrapSubmitButton ().setIcon (EDefaultIcon.YES).addChild ("Send IM request"));
    }
  }
}
