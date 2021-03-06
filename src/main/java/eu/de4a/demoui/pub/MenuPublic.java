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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.uicore.page.system.BasePageShowChildren;

/**
 * Menu for the Demo UI.
 *
 * @author Philip Helger
 */
@Immutable
public final class MenuPublic
{
  public static final String MENU_DE = "de";
  public static final String MENU_DE_IM_USER = "de-im-user";
  public static final String MENU_DE_IM_EXPERT = "de-im-expert";
  public static final String MENU_DE_USI_USER = "de-usi-user";
  public static final String MENU_DE_USI_MOCK = "de-usi-mock";

  public static final String MENU_DEMO_UI = "demoui";
  public static final String MENU_SEND_RANDOM_MESSAGE = "sendrandommessage";
  public static final String MENU_SEND_MESSAGE = "sendmessage";
  public static final String MENU_EXAMPLE_MESSAGE = "examplemsg";
  public static final String MENU_VALIDATE_MESSAGE = "validatemsg";

  private MenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // DE stuff
    {
      final IMenuItemPage aDE = aMenuTree.createRootItem (new BasePageShowChildren <> (MENU_DE, "Data Evaluator", aMenuTree));
      aMenuTree.createItem (aDE, new PagePublicDE_IM_User (MENU_DE_IM_USER));
      aMenuTree.createItem (aDE, new PagePublicDE_IM_Expert (MENU_DE_IM_EXPERT));
      aMenuTree.createItem (aDE, new PagePublicDE_USI_User (MENU_DE_USI_USER));
      aMenuTree.createItem (aDE, new PagePublicDE_USI_Mock (MENU_DE_USI_MOCK));
    }

    // Demo UI stuff
    {
      final IMenuItemPage aDemoUI = aMenuTree.createRootItem (new BasePageShowChildren <> (MENU_DEMO_UI, "Demo UI", aMenuTree));
      aMenuTree.createItem (aDemoUI, new PagePublicSendRandomMessage (MENU_SEND_RANDOM_MESSAGE));
      aMenuTree.createItem (aDemoUI, new PagePublicSendMessage (MENU_SEND_MESSAGE));
      aMenuTree.createItem (aDemoUI, new PagePublicCreateRandomMessage (MENU_EXAMPLE_MESSAGE));
      aMenuTree.createItem (aDemoUI, new PagePublicValidateMessage (MENU_VALIDATE_MESSAGE));
    }

    // Set default
    aMenuTree.setDefaultMenuItemID (MENU_DE);
  }
}
