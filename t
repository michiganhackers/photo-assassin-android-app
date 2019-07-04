[33mcommit 97e34202ecc45ce80ee789869866e9dd2b528c60[m[33m ([m[1;36mHEAD -> [m[1;32mprofileScreen[m[33m, [m[1;31morigin/profileScreen[m[33m)[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sun Jun 23 19:06:01 2019 -0500

    Profile Activity styling
    
    Added style for padding left/right/top for screens because I believe all screens will have same padding except for login screens. Login screens padding was seet in LinearLayoutLoginPages, but I moved it to OUtermostLayoutPaddingLoginPages to make it consistent with OutermostLayoutPadding. Pulling the padding out of the linear layout style for the login pages might be overkill.
    Styled everything in the profile activity above the divider, except still don't have "Add Friend" and "Block" buttons.
    Made a "Header" style to be used for the headers across all the screens e.g. Friends, Players, Remaining, Nearby Players, etc.
    Made a reusable layout for the profile image and add image FAB.
    Added ripple effect to back button. One small problem is that the ripple effect on the left side of the back button is cut off because the back button has a negative left margin in order to position is correctly.
    Added me.grantland.autofittextview dependency because I couldn't get android's native autofit to work for edittexts. I believe it might only work for textviews. This was necessary to fit longer display names on the screen. On my phone, only about 9 characters could fit at 36sp, but our character limit is 20. Didn't want to just reduce character limit to 9 because it could vary by screen size.
    Also made a reusable layout for the divider because it is also used in the "invite players" screen.

 app/build.gradle                                                                  |   2 [32m++[m
 app/src/main/java/org/michiganhackers/photoassassin/DisplayName.java              |   6 [32m++++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/Profile/ProfileActivity.java  |  14 [32m+++++++++++[m[31m---[m
 app/src/main/java/org/michiganhackers/photoassassin/Profile/ProfileViewModel.java |   5 [32m++[m[31m---[m
 app/src/main/res/drawable/back_button_background.xml                              |  11 [32m+++++++++++[m
 app/src/main/res/layout/activity_login.xml                                        |   1 [32m+[m
 app/src/main/res/layout/activity_profile.xml                                      | 116 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m---------------------------------------------------------[m
 app/src/main/res/layout/activity_registration.xml                                 |   1 [32m+[m
 app/src/main/res/layout/activity_reset_password.xml                               |  10 [32m+++++[m[31m-----[m
 app/src/main/res/layout/activity_setup_profile.xml                                |  26 [32m++[m[31m------------------------[m
 app/src/main/res/layout/horizontal_divider.xml                                    |  14 [32m++++++++++++++[m
 app/src/main/res/layout/profile_picture_image_and_fab.xml                         |  33 [32m+++++++++++++++++++++++++++++++++[m
 app/src/main/res/values/colors.xml                                                |   4 [32m+++[m[31m-[m
 app/src/main/res/values/strings.xml                                               |   2 [32m++[m
 app/src/main/res/values/styles.xml                                                |  44 [32m+++++++++++++++++++++++++++++++++++++++++++[m[31m-[m
 15 files changed, 193 insertions(+), 96 deletions(-)

[33mcommit b6328f2bbd6bff2c5286e2bb914cf86285cb5483[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 22 16:17:17 2019 -0500

    Generalized button and edit text styles
    
    the button and edit text styles that were used for login pages can be used everywhere in the app, so I renamed them to signify that. I also created a background for the white buttons without a border.

 app/src/main/res/drawable-v21/{login_page_button_background.xml => transparent_white_border_button_background.xml} |  0
 app/src/main/res/drawable/transparent_white_button_background.xml                                                  | 27 [32m+++++++++++++++++++++++++++[m
 app/src/main/res/drawable/{login_page_edit_text_background.xml => transparent_white_edit_text_background.xml}      |  0
 app/src/main/res/layout/activity_login.xml                                                                         |  6 [32m+++[m[31m---[m
 app/src/main/res/layout/activity_profile.xml                                                                       | 33 [32m++++++++++++++++++++++++++++++++[m[31m-[m
 app/src/main/res/layout/activity_registration.xml                                                                  |  4 [32m++[m[31m--[m
 app/src/main/res/layout/activity_reset_password.xml                                                                |  6 [32m+++[m[31m---[m
 app/src/main/res/layout/activity_setup_profile.xml                                                                 | 10 [32m+++++[m[31m-----[m
 app/src/main/res/layout/edit_text_email.xml                                                                        |  6 [32m+++[m[31m---[m
 app/src/main/res/layout/edit_text_password.xml                                                                     |  6 [32m+++[m[31m---[m
 app/src/main/res/values/strings.xml                                                                                |  1 [32m+[m
 app/src/main/res/values/styles.xml                                                                                 | 58 [32m+++++++++++++++++++++++++++++++[m[31m---------------------------[m
 12 files changed, 110 insertions(+), 47 deletions(-)

[33mcommit a522d3768fbc0a2b2f0b6950edad6382597b6c9f[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 8 15:49:39 2019 -0500

    Added left arrow PNGs
    
    Android doesn't support drop shadows on SVGs. It's not possible to import an SVG with drop shadow. Importing a png from figma's svg was resulting in blurry images. Wasn't able to convert svg to vector drawable xml either. make python script to create pngs of multiple resolutions from svg using inkscape. the resulting pngs have higher resolutions than normal, but still look slightly blurry to me. only other option is to just not have drop shadow on any icons.

 app/src/main/res/drawable-hdpi/ic_left_arrow.png    | Bin [31m0[m -> [32m3850[m bytes
 app/src/main/res/drawable-mdpi/ic_left_arrow.png    | Bin [31m0[m -> [32m2108[m bytes
 app/src/main/res/drawable-xhdpi/ic_left_arrow.png   | Bin [31m0[m -> [32m5716[m bytes
 app/src/main/res/drawable-xxhdpi/ic_left_arrow.png  | Bin [31m0[m -> [32m9965[m bytes
 app/src/main/res/drawable-xxxhdpi/ic_left_arrow.png | Bin [31m0[m -> [32m14646[m bytes
 app/src/main/res/layout/activity_profile.xml        |  20 [32m++++++++++++++++++++[m
 app/src/main/res/values/strings.xml                 |   1 [32m+[m
 svg_to_png_export_for_android.py                    |  29 [32m+++++++++++++++++++++++++++++[m
 8 files changed, 50 insertions(+)

[33mcommit e93472f5aa3b7fef3493ff1f4477a255b9a84e8c[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 8 11:50:53 2019 -0500

    update profile pic and display name working
    
    The way to edit the display name is by clicking pencil icon next to edittext. It saves or displays error snackbar if you click outside of edit text or click enter in keyboard. If you try to swipe away snackbar displayed above keyboard, it will recognize it as clicking away from edit text and close the keyboard.

 app/src/main/AndroidManifest.xml                                                               |   2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/DisplayName.java                           |  33 [32m++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/{LoginPages => }/Email.java                |   9 [32m+[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java              |   2 [32m++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java       |   5 [32m+[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java      |   2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/SetupProfileActivity.java       |  30 [32m+++++[m[31m-----------[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                          |   4 [32m+[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/{LoginPages => }/Password.java             |   2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/Profile/ProfileActivity.java               | 205 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/Profile/ProfileViewModel.java              | 118 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/{ => Profile}/ProfileViewModelFactory.java |  16 [32m+++[m[31m------[m
 app/src/main/java/org/michiganhackers/photoassassin/ProfileActivity.java                       |  86 [31m----------------------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/ProfileViewModel.java                      | 155 [31m-----------------------------------------------------------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/RequestImageDialog.java                    |   6 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/User.java                                  |   4 [31m---[m
 app/src/main/res/layout/activity_profile.xml                                                   | 284 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m--------------------------------------------------------------------------[m
 17 files changed, 529 insertions(+), 434 deletions(-)

[33mcommit 9144a8952f49e5c188c678a4c95e0bf016f86123[m
Merge: 8cd012b 03afa4a
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 1 19:23:54 2019 -0500

    TEMP SAVE. DOESN't WORK
    
    Merge branch 'loginAndRegistration' into profileScreen

[33mcommit 03afa4a191a9490722a5d094a8f9ba8009ce9542[m[33m ([m[1;31morigin/loginAndRegistration[m[33m, [m[1;32mloginAndRegistration[m[33m)[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 1 14:49:09 2019 -0500

    Cleanup
    
    Removed prependToException utility method.
    Changed acct -> account to prevent confusion about abbreviation.

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java        |  6 [32m+++[m[31m---[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java  | 26 [32m+++++++++++[m[31m---------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/SetupProfileActivity.java | 18 [32m++[m[31m----------------[m
 app/src/main/java/org/michiganhackers/photoassassin/Util.java                            |  5 [31m-----[m
 app/src/main/res/values/strings.xml                                                      |  2 [31m--[m
 6 files changed, 17 insertions(+), 42 deletions(-)

[33mcommit ff12053f17cc2888e38701ab7f13c7ca6c38052a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 1 14:26:32 2019 -0500

    Handled double registration case
    
    If a user tries to register with facebook or google but already has an account with that service, just log them in without overwriting any data.

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java        | 44 [32m+++++++++++++++++++++++++++++++++++++[m[31m-------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  6 [32m++++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java  | 44 [32m+++[m[31m-----------------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/SetupProfileActivity.java |  2 [32m+[m[31m-[m
 4 files changed, 45 insertions(+), 51 deletions(-)

[33mcommit 1b8af61c95204e2a72c00817f06e2eb4b862c152[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 1 13:58:55 2019 -0500

    Unregistered users can't login w/ google/fb
    
    Handled case where an unregistered user tries to login with google or facebook in the login page. In that case, they are taken to the SetupProfile activity.

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  8 [32m+[m[31m-------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java  | 45 [32m+++++++++++++++++++++++++++++++++++++++++++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/SetupProfileActivity.java |  6 [32m++++++[m
 app/src/main/res/values/strings.xml                                                      |  1 [32m+[m
 4 files changed, 51 insertions(+), 9 deletions(-)

[33mcommit 86a918733143ffd6b24efaa9004ac92f5844c6a3[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jun 1 12:05:23 2019 -0500

    First draft of SetupProfileActivity
    
    Added SetupProfileActivity before RegistrationActivity.
    Removed id's from layout views that are never referenced from java code.
    Added firebase storage, firestore, and bumptech glide dependencies.
    Updated gradle dependency.
    Added callbacks for ServiceLoginHandler so LoginActivity and RegistrationActivity could do different things after successful login/registration. This also makes it so ServiceLoginHandler doesn't need a reference to the coordinator layout to show error messages to user.
    Created preprendToException utility method so more info can be prepended to an exception's message before propogating it. I might removed this.
    Removed redundant check in Password.getError() method.
    Created User class.
    Created pojoToMap utility so objects can be converted into maps to add that data to cloud firestore.
    Created RequestImageDialog for SetupProfileActivity. It will also be used to update profile picture from profile screen.
    Added file provider path for the root directory of files-path.

 app/build.gradle                                                                          |   4 [32m++++[m
 app/src/main/AndroidManifest.xml                                                          |  17 [32m+++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         |  39 [32m+++++++++++++++++++++++++++++++[m[31m--------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/Password.java              |   3 [31m---[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  | 121 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m--------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java |   4 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java   |  68 [32m++++++++++++++++++++++++++++++++++++[m[31m--------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/SetupProfileActivity.java  | 154 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                     |   2 [32m++[m
 app/src/main/java/org/michiganhackers/photoassassin/RequestImageDialog.java               | 135 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/User.java                             | 135 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/Util.java                             |  30 [32m++++++++++++++++++++++++++++++[m
 app/src/main/res/drawable-anydpi/ic_add_photo.xml                                         |  13 [32m+++++++++++++[m
 app/src/main/res/drawable-anydpi/ic_camera.xml                                            |  13 [32m+++++++++++++[m
 app/src/main/res/drawable-anydpi/ic_gallery.xml                                           |  10 [32m++++++++++[m
 app/src/main/res/drawable-anydpi/ic_profile.xml                                           |  10 [32m++++++++++[m
 app/src/main/res/drawable-hdpi/ic_add_photo.png                                           | Bin [31m0[m -> [32m336[m bytes
 app/src/main/res/drawable-hdpi/ic_camera.png                                              | Bin [31m0[m -> [32m349[m bytes
 app/src/main/res/drawable-hdpi/ic_gallery.png                                             | Bin [31m0[m -> [32m275[m bytes
 app/src/main/res/drawable-hdpi/ic_profile.png                                             | Bin [31m0[m -> [32m251[m bytes
 app/src/main/res/drawable-mdpi/ic_add_photo.png                                           | Bin [31m0[m -> [32m211[m bytes
 app/src/main/res/drawable-mdpi/ic_camera.png                                              | Bin [31m0[m -> [32m229[m bytes
 app/src/main/res/drawable-mdpi/ic_gallery.png                                             | Bin [31m0[m -> [32m192[m bytes
 app/src/main/res/drawable-mdpi/ic_profile.png                                             | Bin [31m0[m -> [32m184[m bytes
 app/src/main/res/drawable-xhdpi/ic_add_photo.png                                          | Bin [31m0[m -> [32m376[m bytes
 app/src/main/res/drawable-xhdpi/ic_camera.png                                             | Bin [31m0[m -> [32m430[m bytes
 app/src/main/res/drawable-xhdpi/ic_gallery.png                                            | Bin [31m0[m -> [32m343[m bytes
 app/src/main/res/drawable-xhdpi/ic_profile.png                                            | Bin [31m0[m -> [32m305[m bytes
 app/src/main/res/drawable-xxhdpi/ic_add_photo.png                                         | Bin [31m0[m -> [32m572[m bytes
 app/src/main/res/drawable-xxhdpi/ic_camera.png                                            | Bin [31m0[m -> [32m669[m bytes
 app/src/main/res/drawable-xxhdpi/ic_gallery.png                                           | Bin [31m0[m -> [32m512[m bytes
 app/src/main/res/drawable-xxhdpi/ic_profile.png                                           | Bin [31m0[m -> [32m442[m bytes
 app/src/main/res/drawable/request_image_dialog_background.xml                             |   9 [32m+++++++++[m
 app/src/main/res/layout/activity_login.xml                                                |   3 [31m---[m
 app/src/main/res/layout/activity_registration.xml                                         |  15 [32m++[m[31m-------------[m
 app/src/main/res/layout/activity_reset_password.xml                                       |   3 [31m---[m
 app/src/main/res/layout/activity_setup_profile.xml                                        |  83 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/layout/request_image_dialog.xml                                          |  63 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/values/colors.xml                                                        |   2 [32m++[m
 app/src/main/res/values/strings.xml                                                       |  15 [32m++++++++++++++[m[31m-[m
 app/src/main/res/values/styles.xml                                                        |  15 [32m+++++++++++++++[m
 app/src/main/res/xml/file_provider_paths.xml                                              |   6 [32m++++++[m
 build.gradle                                                                              |   2 [32m+[m[31m-[m
 gradle.properties                                                                         |   1 [32m+[m
 44 files changed, 877 insertions(+), 98 deletions(-)

[33mcommit 8cd012b959d1fa008a17104f2c84a4fc12fe53b4[m
Merge: 53105e9 0e5bb7f
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Mon May 20 14:32:49 2019 -0400

    Merge branch 'master' into profileScreen

[33mcommit 0e5bb7ff6ced431a40b19eeee0f53acf3694fafe[m[33m ([m[1;31morigin/master[m[33m, [m[1;31morigin/HEAD[m[33m, [m[1;32mmaster[m[33m)[m
Merge: 309983a e6b7219
Author: Glavon <owaink2255@gmail.com>
Date:   Mon May 20 12:41:47 2019 -0400

    Merge pull request #16 from michiganhackers/loginAndRegistration
    
    Login screens

[33mcommit 53105e9c0e765bf300b129f47d179d0fe1d4637a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sun May 19 20:22:36 2019 -0400

    Profile screen WIP
    
    Just wanted to save this because going to remove the user creation logic in profile and create another step in the registration process.
    Basics of profile viewmodel done.
    first draft of layout of profile pic and user stats done.

 app/build.gradle                                                                         |   2 [32m++[m
 app/src/main/AndroidManifest.xml                                                         |   1 [32m+[m
 app/src/main/java/org/michiganhackers/photoassassin/FirebaseAuthActivity.java            |   4 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/Email.java                |   7 [32m++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |   4 [32m++++[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                    |   7 [32m+++++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/ProfileActivity.java                 |  86 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/ProfileViewModel.java                | 183 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/ProfileViewModelFactory.java         |  29 [32m+++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/User.java                            | 115 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/Util.java                            |  25 [32m++++++++++++++++++++[m
 app/src/main/res/drawable-anydpi/ic_add_photo.xml                                        |  13 [32m+++++++++++[m
 app/src/main/res/drawable-anydpi/ic_pencil.xml                                           |  10 [32m++++++++[m
 app/src/main/res/drawable-anydpi/ic_profile.xml                                          |  10 [32m++++++++[m
 app/src/main/res/drawable-hdpi/ic_add_photo.png                                          | Bin [31m0[m -> [32m336[m bytes
 app/src/main/res/drawable-hdpi/ic_pencil.png                                             | Bin [31m0[m -> [32m224[m bytes
 app/src/main/res/drawable-hdpi/ic_profile.png                                            | Bin [31m0[m -> [32m251[m bytes
 app/src/main/res/drawable-mdpi/ic_add_photo.png                                          | Bin [31m0[m -> [32m211[m bytes
 app/src/main/res/drawable-mdpi/ic_pencil.png                                             | Bin [31m0[m -> [32m160[m bytes
 app/src/main/res/drawable-mdpi/ic_profile.png                                            | Bin [31m0[m -> [32m184[m bytes
 app/src/main/res/drawable-xhdpi/ic_add_photo.png                                         | Bin [31m0[m -> [32m376[m bytes
 app/src/main/res/drawable-xhdpi/ic_pencil.png                                            | Bin [31m0[m -> [32m250[m bytes
 app/src/main/res/drawable-xhdpi/ic_profile.png                                           | Bin [31m0[m -> [32m305[m bytes
 app/src/main/res/drawable-xxhdpi/ic_add_photo.png                                        | Bin [31m0[m -> [32m572[m bytes
 app/src/main/res/drawable-xxhdpi/ic_pencil.png                                           | Bin [31m0[m -> [32m319[m bytes
 app/src/main/res/drawable-xxhdpi/ic_profile.png                                          | Bin [31m0[m -> [32m442[m bytes
 app/src/main/res/layout/activity_main.xml                                                |  25 [32m++++++++++++++++[m[31m----[m
 app/src/main/res/layout/activity_profile.xml                                             | 200 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/values/strings.xml                                                      |   6 [32m+++++[m
 app/src/main/res/values/styles.xml                                                       |   2 [32m+[m[31m-[m
 30 files changed, 720 insertions(+), 9 deletions(-)

[33mcommit e6b72192af1ebb9939dd8e01d0322aa0e9d2c09a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 18 21:02:45 2019 -0400

    Added background to app theme

 app/src/main/res/values/styles.xml | 2 [32m+[m[31m-[m
 1 file changed, 1 insertion(+), 1 deletion(-)

[33mcommit 849158d494dce9e43a89c3e52b68bc6831b3a17a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 18 17:00:43 2019 -0400

    Color fixes
    
    Fixed title shadow and opacity of white colors.

 app/src/main/res/drawable-v21/login_page_button_background.xml |  6 [32m+++[m[31m---[m
 app/src/main/res/drawable/login_page_edit_text_background.xml  |  2 [32m+[m[31m-[m
 app/src/main/res/layout/activity_login.xml                     |  8 [32m++++[m[31m----[m
 app/src/main/res/layout/activity_registration.xml              |  4 [32m++[m[31m--[m
 app/src/main/res/layout/edit_text_email.xml                    |  4 [32m++[m[31m--[m
 app/src/main/res/layout/edit_text_password.xml                 |  4 [32m++[m[31m--[m
 app/src/main/res/values/colors.xml                             | 10 [32m+++++++[m[31m---[m
 app/src/main/res/values/styles.xml                             | 10 [32m++++++[m[31m----[m
 8 files changed, 27 insertions(+), 21 deletions(-)

[33mcommit 5cea1622a597695da314c787e146b97a5a6411a2[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 18 16:05:15 2019 -0400

    cleanup
    
    Updated dependencies.
    Changed facebook sdk dependency to just the login part.
    Added logging where EditText getText() == null. This happens when getText() is called during construction. I'm still not sure when this would actually happen: https://github.com/aosp-mirror/platform_frameworks_base/blob/762650514da7656968b4490703e4b79361436473/core/java/android/widget/EditText.java#L109
    Decided not to add feature to link google, fb, and email accounts because it isn't necessary for v1.
    Removed deprecated FacebookSdk.sdkInitialize call. sdk is now automatically initialized: https://developers.facebook.com/docs/android/upgrading-4x#4180to4190
    Reworded failed registration message.
    
    Please enter the commit message for your changes. Lines starting

 app/build.gradle                                                                          | 10 [32m+++++[m[31m-----[m
 app/src/main/AndroidManifest.xml                                                          |  2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         |  5 [32m++[m[31m---[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  |  4 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java |  5 [32m+++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java   |  2 [31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLogoutHandler.java  |  2 [31m--[m
 app/src/main/res/values/strings.xml                                                       |  2 [32m+[m[31m-[m
 8 files changed, 14 insertions(+), 18 deletions(-)

[33mcommit 5951c84c297cf9e8c72f81ca1206807774985ee4[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu May 16 19:39:48 2019 -0400

    Style fixes
    
    Fix size of most everything after realizing that the sizes in figma corresponded to dp and sp.
    Increased size of textinputlayout hint and error text.
    Fixed opacity of the white colors in the login pages.
    Currently, stroke weight and left margins on edit text hints are each the same across all login pages. This is not the case for the figma design, and I haven't yet figured out if this is a mistake.
    The register with google and register with facebook buttons on the registration page also don't quite match teh figma design yet.

 app/src/main/res/drawable-v21/login_page_button_background.xml | 12 [32m++++++[m[31m------[m
 app/src/main/res/drawable/login_page_edit_text_background.xml  |  4 [32m++[m[31m--[m
 app/src/main/res/layout/activity_login.xml                     | 35 [32m++++++++++++++++[m[31m-------------------[m
 app/src/main/res/layout/activity_registration.xml              | 70 [32m+++++++++++++++++++++++++++++++++++++++++++++[m[31m-------------------------[m
 app/src/main/res/layout/activity_reset_password.xml            |  7 [32m+++[m[31m----[m
 app/src/main/res/layout/edit_text_email.xml                    |  1 [32m+[m
 app/src/main/res/layout/edit_text_password.xml                 |  5 [32m+++[m[31m--[m
 app/src/main/res/values/colors.xml                             |  4 [32m++[m[31m--[m
 app/src/main/res/values/styles.xml                             | 31 [32m++++++++++++++++++[m[31m-------------[m
 9 files changed, 96 insertions(+), 73 deletions(-)

[33mcommit 65e3d9dcc88777e5cb258f370be84a6849098073[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed May 15 21:18:39 2019 -0400

    Style fixes
    
    Fixed spacing, title sizes, and link button fonts.
    Currently working on fixing size of google and facebook logos on registration page. Just working with google button right now.

 app/src/main/res/drawable-v21/login_page_button_background.xml |   4 [32m++[m[31m--[m
 app/src/main/res/drawable/login_page_edit_text_background.xml  |   4 [32m++[m[31m--[m
 app/src/main/res/font/asap.ttf                                 | Bin [31m0[m -> [32m131180[m bytes
 app/src/main/res/layout/activity_login.xml                     |  35 [32m+++++++++++++++++[m[31m------------------[m
 app/src/main/res/layout/activity_registration.xml              |  44 [32m++++++++++++++++++++++++++++[m[31m----------------[m
 app/src/main/res/layout/activity_reset_password.xml            |  24 [32m+++++++++++++[m[31m-----------[m
 app/src/main/res/layout/edit_text_email.xml                    |   1 [31m-[m
 app/src/main/res/layout/edit_text_password.xml                 |   4 [32m++[m[31m--[m
 app/src/main/res/values/styles.xml                             |  20 [32m+++++++++++++++[m[31m-----[m
 9 files changed, 79 insertions(+), 57 deletions(-)

[33mcommit 4b367a717b8ef96d15f01a1b6d11639784a0478a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed May 8 18:12:00 2019 -0400

    Reduced opacity of some stuff

 app/src/main/res/drawable-v21/login_page_button_background.xml |  6 [32m+++[m[31m---[m
 app/src/main/res/drawable/login_page_edit_text_background.xml  |  2 [32m+[m[31m-[m
 app/src/main/res/layout/activity_login.xml                     | 14 [32m++++++++[m[31m------[m
 app/src/main/res/layout/activity_registration.xml              |  3 [32m+++[m
 app/src/main/res/layout/activity_reset_password.xml            |  6 [32m++++[m[31m--[m
 app/src/main/res/layout/edit_text_email.xml                    | 10 [32m+++++[m[31m-----[m
 app/src/main/res/layout/edit_text_password.xml                 |  4 [32m++[m[31m--[m
 app/src/main/res/values/colors.xml                             |  4 [32m+++[m[31m-[m
 app/src/main/res/values/styles.xml                             |  2 [32m+[m[31m-[m
 9 files changed, 30 insertions(+), 21 deletions(-)

[33mcommit efe68e9414e2aa392b3a1ae14ffe572f68fc4e76[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed May 8 17:08:35 2019 -0400

    Now can use custom fonts in resource files
    
    Instead of creating a custom view and setting the font, can specify the fontFamily in the layout file.
    Added bold font and shadows to font where needed.
    Made style for login page titles.
    Update gradle 3.3.2 -> 3.4.0

 app/src/main/AndroidManifest.xml                                                             |   3 [32m+++[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomButton.java            |  23 [31m-----------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomEditText.java          |  23 [31m-----------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomTextInputEditText.java |  23 [31m-----------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomTextView.java          |  23 [31m-----------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/TypefaceUtil.java            |  18 [31m------------------[m
 app/src/main/res/font/economica.ttf                                                          | Bin [31m0[m -> [32m26756[m bytes
 app/src/main/res/font/economica_bold.xml                                                     |   7 [32m+++++++[m
 app/src/main/res/layout/activity_login.xml                                                   |  39 [32m++++++++++++++++++++[m[31m-------------------[m
 app/src/main/res/layout/activity_registration.xml                                            |  15 [32m+++++++[m[31m--------[m
 app/src/main/res/layout/activity_reset_password.xml                                          |  26 [32m+++++++++++[m[31m---------------[m
 app/src/main/res/layout/edit_text_email.xml                                                  |   4 [32m++[m[31m--[m
 app/src/main/res/layout/edit_text_password.xml                                               |   2 [32m+[m[31m-[m
 app/src/main/res/values/font_certs.xml                                                       |  17 [32m+++++++++++++++++[m
 app/src/main/res/values/preloaded_fonts.xml                                                  |   6 [32m++++++[m
 app/src/main/res/values/styles.xml                                                           |  12 [32m++++++++++++[m
 build.gradle                                                                                 |   2 [32m+[m[31m-[m
 gradle/wrapper/gradle-wrapper.properties                                                     |   4 [32m++[m[31m--[m
 18 files changed, 89 insertions(+), 158 deletions(-)

[33mcommit c0a51b57be5120d8aa52d21955d4e57de4a475d2[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 23:18:59 2019 -0400

    error text styling
    
    added padding to the top of edit text background so hint didn't overlap with background.
    errors now show for both email and password as the same time.

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         |  9 [32m+++++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  |  9 [32m+++++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java |  1 [32m+[m
 app/src/main/res/drawable/login_page_edit_text_background.xml                             | 14 [32m++++++++++++++[m
 app/src/main/res/drawable/rounded_corner_background.xml                                   | 10 [31m----------[m
 app/src/main/res/values/styles.xml                                                        |  2 [32m+[m[31m-[m
 6 files changed, 26 insertions(+), 19 deletions(-)

[33mcommit 305f8bec9f6843785b862dc95b004acb91b38d0b[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 22:38:47 2019 -0400

    Added ripple effect to buttons

 app/src/main/res/drawable-v21/login_page_button_background.xml         | 32 [32m++++++++++++++++++++++++++++++++[m
 app/src/main/res/drawable/{bg_gradient.xml => gradient_background.xml} |  0
 app/src/main/res/drawable/rounded_corner_with_border_background.xml    | 11 [31m-----------[m
 app/src/main/res/values/styles.xml                                     |  6 [32m+++[m[31m---[m
 4 files changed, 35 insertions(+), 14 deletions(-)

[33mcommit 700ac3e76c277109e1f53ad9c3b5190e0bad276a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 21:55:18 2019 -0400

    Swapped continue w/ google and fb buttons

 app/src/main/res/layout/activity_login.xml | 20 [32m++++++++++[m[31m----------[m
 1 file changed, 10 insertions(+), 10 deletions(-)

[33mcommit 90dba0154764fc558b240568c5809988dc4ef116[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 21:52:17 2019 -0400

    Made login pages scrollable
    
    Allows user to scroll if cannot see all the buttons when keyboard is up.

 app/src/main/res/layout/activity_login.xml          | 246 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m-----------------------------------------------------------------------------------------------[m
 app/src/main/res/layout/activity_registration.xml   | 109 [32m+++++++++++++++++++++++++++++++++++++++++++++[m[31m-----------------------------------------[m
 app/src/main/res/layout/activity_reset_password.xml | 104 [32m++++++++++++++++++++++++++++++++++++++++++++[m[31m---------------------------------------[m
 3 files changed, 239 insertions(+), 220 deletions(-)

[33mcommit 774749b4b5effc0bcf69ab54ea17a65a707e7da9[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 21:45:52 2019 -0400

    Removed password toggle and character count

 app/src/main/res/layout/edit_text_password.xml | 8 [32m+[m[31m-------[m
 1 file changed, 1 insertion(+), 7 deletions(-)

[33mcommit c5eb8a8be95b9abada89ac5efd06093dff713f7f[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 21:35:52 2019 -0400

    Added progress bar

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         | 12 [32m++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  | 13 [32m+++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java | 13 [32m+++++++++++++[m
 app/src/main/res/layout/activity_login.xml                                                |  8 [32m++++++++[m
 app/src/main/res/layout/activity_registration.xml                                         |  8 [32m+++++++[m[31m-[m
 app/src/main/res/layout/activity_reset_password.xml                                       |  8 [32m++++++++[m
 6 files changed, 61 insertions(+), 1 deletion(-)

[33mcommit f7a8a27430f58a4ccd9cbf123ce712168cb26e29[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 21:01:37 2019 -0400

    Styling, ResetPwdActivity finishes after send

 app/src/main/java/org/michiganhackers/photoassassin/FirebaseAuthActivity.java             | 2 [32m++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/Email.java                 | 1 [31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         | 4 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/Password.java              | 1 [31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  | 5 [32m+[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java | 2 [32m+[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java   | 6 [32m++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLogoutHandler.java  | 1 [31m-[m
 8 files changed, 8 insertions(+), 14 deletions(-)

[33mcommit f1c22f764fe187502d08a726865ff1c0b8fbd260[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 20:28:34 2019 -0400

    Made error text black
    
    Not sure if this is the best color to use, but the default red color was hard to see against the background

 app/src/main/res/layout/edit_text_email.xml    | 5 [32m+++[m[31m--[m
 app/src/main/res/layout/edit_text_password.xml | 1 [32m+[m
 app/src/main/res/values/styles.xml             | 4 [32m++++[m
 3 files changed, 8 insertions(+), 2 deletions(-)

[33mcommit df0e4859c8ce2b3dade2c58bf7fb59e0b29d50b6[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 20:15:13 2019 -0400

    Added ResetPasswordActivity
    
    Also fixed some font sizes.

 app/src/main/AndroidManifest.xml                                                          |  7 [32m+++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java         | 14 [32m++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java  |  4 [32m+++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ResetPasswordActivity.java | 75 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java   | 26 [32m++++++++++++++++[m[31m----------[m
 app/src/main/res/layout/activity_login.xml                                                |  1 [32m+[m
 app/src/main/res/layout/activity_registration.xml                                         |  5 [32m++[m[31m---[m
 app/src/main/res/layout/activity_reset_password.xml                                       | 52 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/values/strings.xml                                                       |  5 [32m++++[m[31m-[m
 9 files changed, 170 insertions(+), 19 deletions(-)

[33mcommit 130bc4290a10dfb1e071c37727173d4f41982182[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 18:53:17 2019 -0400

    Added login capability to LoginActivity

 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java        | 56 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  3 [32m+[m[31m--[m
 app/src/main/res/values/strings.xml                                                      |  1 [32m+[m
 3 files changed, 56 insertions(+), 4 deletions(-)

[33mcommit 369270ff8e4a28bdf855ef563550bb9a3c19d663[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat May 4 16:57:07 2019 -0400

    Split ServiceLoginHandler and ServiceLogoutHandler
    
    Needed to log out of services in FirebaseAuthActivity, but ServiceLoginHandler requires a CoordinatorLayout which FirebaseAuthActivity doesn't have.

 app/src/main/java/org/michiganhackers/photoassassin/FirebaseAuthActivity.java            | 18 [32m+++++++++++[m[31m-------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java        | 43 [32m++++++++++++++++++++++++++++++++++++++++++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  7 [32m+++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java  | 26 [32m++++++[m[31m--------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLogoutHandler.java | 51 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                    |  2 [32m+[m[31m-[m
 app/src/main/res/layout/activity_login.xml                                               |  8 [32m++++++[m[31m--[m
 app/src/main/res/layout/activity_main.xml                                                |  2 [32m+[m[31m-[m
 8 files changed, 121 insertions(+), 36 deletions(-)

[33mcommit 4761311430bd21f31d4184dcc87d751a3dfd7d95[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Fri May 3 23:05:57 2019 -0400

    working on sign-in logic
    
    Added FirebaseAuthActivity for activities that must ensure that a user is signed in.
    Added signout button in mainactivity for testing.
    Moved sign in with google/facebook to ServiceLoginHandler class so the same logic can be used for LoginActivity.
    Currently incorrectly signing out, causing app to crash when trying to sign out 2x in a row.

 app/src/main/java/org/michiganhackers/photoassassin/FirebaseAuthActivity.java            |  58 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/LoginActivity.java        |   1 [32m+[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java | 120 [32m+++++++++++[m[31m---------------------------------------------------------------------------------------------------------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/ServiceLoginHandler.java  | 163 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                    |  13 [32m+++++++++++[m[31m--[m
 app/src/main/res/layout/activity_main.xml                                                |  10 [32m+++++++++[m[31m-[m
 6 files changed, 253 insertions(+), 112 deletions(-)

[33mcommit c8755966f79db84de4b9d6c0831557ff610d2021[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Fri May 3 21:56:55 2019 -0400

    login and registration page styling
    
    added gradient, but it doesn't look exactly like design yet.
    made text white.
    added logos to google and facebook sign in buttons.
    fixed some spacing between views.

 app/build.gradle                                                                         |   1 [32m+[m
 app/src/main/AndroidManifest.xml                                                         |  34 [32m+++++++++++++++++++++++++++[m[31m-------[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginPages/RegistrationActivity.java |  66 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m-[m
 app/src/main/res/drawable-hdpi/ic_facebook_f.png                                         | Bin [31m0[m -> [32m463[m bytes
 app/src/main/res/drawable-hdpi/ic_google_g.png                                           | Bin [31m0[m -> [32m921[m bytes
 app/src/main/res/drawable-mdpi/ic_facebook_f.png                                         | Bin [31m0[m -> [32m327[m bytes
 app/src/main/res/drawable-mdpi/ic_google_g.png                                           | Bin [31m0[m -> [32m545[m bytes
 app/src/main/res/drawable-xhdpi/ic_facebook_f.png                                        | Bin [31m0[m -> [32m582[m bytes
 app/src/main/res/drawable-xhdpi/ic_google_g.png                                          | Bin [31m0[m -> [32m1346[m bytes
 app/src/main/res/drawable-xxhdpi/ic_facebook_f.png                                       | Bin [31m0[m -> [32m882[m bytes
 app/src/main/res/drawable-xxhdpi/ic_google_g.png                                         | Bin [31m0[m -> [32m2357[m bytes
 app/src/main/res/drawable-xxxhdpi/ic_facebook_f.png                                      | Bin [31m0[m -> [32m1208[m bytes
 app/src/main/res/drawable-xxxhdpi/ic_google_g.png                                        | Bin [31m0[m -> [32m3548[m bytes
 app/src/main/res/drawable/bg_gradient.xml                                                |  17 [32m+++++++++++++++++[m
 app/src/main/res/drawable/rounded_corner_background.xml                                  |   2 [32m+[m[31m-[m
 app/src/main/res/drawable/rounded_corner_with_border_background.xml                      |   6 [32m+++[m[31m---[m
 app/src/main/res/layout/activity_login.xml                                               | 147 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m-------------------------------[m
 app/src/main/res/layout/activity_main.xml                                                |   4 [32m+[m[31m---[m
 app/src/main/res/layout/activity_registration.xml                                        |  17 [32m+++++++++++++[m[31m----[m
 app/src/main/res/layout/edit_text_email.xml                                              |   4 [32m+++[m[31m-[m
 app/src/main/res/layout/edit_text_password.xml                                           |   7 [32m++++++[m[31m-[m
 app/src/main/res/layout/linear_layout_app_name.xml                                       |  19 [31m-------------------[m
 app/src/main/res/values/colors.xml                                                       |   7 [32m++++[m[31m---[m
 app/src/main/res/values/strings.xml                                                      |  19 [32m++++++++++++++++[m[31m---[m
 app/src/main/res/values/styles.xml                                                       |  14 [32m+++++++++++++[m[31m-[m
 build.gradle                                                                             |   5 [32m+++[m[31m--[m
 26 files changed, 289 insertions(+), 80 deletions(-)

[33mcommit 362c4548485ee7c1cf70510bc8376700f33938ce[m
Merge: 089d199 46a7262
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Apr 18 20:03:05 2019 -0400

    Merge branch 'loginAndRegistration' of https://github.com/michiganhackers/photo-assassin-android-app into loginAndRegistration

[33mcommit 46a726273c2995903412f45444ad8863b5a6f751[m
Author: tandunn <tandunn@umich.edu>
Date:   Thu Apr 11 21:36:17 2019 -0400

    standardized UI to match registration page

 app/src/main/res/layout/activity_login.xml | 63 [32m+++++++++++[m[31m----------------------------------------------------[m
 1 file changed, 11 insertions(+), 52 deletions(-)

[33mcommit 48865513ce20e812cced00fae3d289a53100f391[m
Merge: 74173fc 8d1d4d2
Author: tandunn <tandunn@umich.edu>
Date:   Thu Apr 11 20:11:59 2019 -0400

    login page progress

[33mcommit 74173fce9d82754764c4b6d0b7687d536a8be08d[m
Author: tandunn <tandunn@umich.edu>
Date:   Thu Apr 11 20:09:18 2019 -0400

    Login page progress

 app/src/main/java/org/michiganhackers/photoassassin/LoginActivity.java |  6 [32m++++++[m
 app/src/main/res/layout/activity_login.xml                             | 87 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m--------------------------[m
 build.gradle                                                           |  2 [32m+[m[31m-[m
 3 files changed, 68 insertions(+), 27 deletions(-)

[33mcommit 089d199ff4e821e31b82c304f4966c7c305c299a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Apr 11 19:47:28 2019 -0400

    Made packe for login pages

 app/src/main/AndroidManifest.xml                                                               | 4 [32m++[m[31m--[m
 app/src/main/java/org/michiganhackers/photoassassin/{ => LoginPages}/Email.java                | 4 [32m+++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/{ => LoginPages}/LoginActivity.java        | 4 [32m+++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/{ => LoginPages}/Password.java             | 4 [32m+++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/{ => LoginPages}/RegistrationActivity.java | 7 [32m+++[m[31m----[m
 app/src/main/res/layout/activity_login.xml                                                     | 2 [32m+[m[31m-[m
 app/src/main/res/layout/activity_registration.xml                                              | 2 [32m+[m[31m-[m
 7 files changed, 16 insertions(+), 11 deletions(-)

[33mcommit 8d1d4d27669cf701938228ea5d75f68d6fb0624e[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed Apr 10 21:56:39 2019 -0400

    Worked on registration page design.
    
    Not sure if title of page should be "registration" like figma shows or the app name like the other login pages.
    Added custom views to be able to use custom font.
    Made styles for login page views.
    Made reusable resources for login pages.
    Made backgrounds for round edittexts and buttons.

 app/src/main/assets/fonts/Economica-Bold-OTF.otf                                             | Bin [31m0[m -> [32m21140[m bytes
 app/src/main/assets/fonts/Economica-BoldItalic-OTF.otf                                       | Bin [31m0[m -> [32m21964[m bytes
 app/src/main/assets/fonts/Economica-Italic-OTF.otf                                           | Bin [31m0[m -> [32m22188[m bytes
 app/src/main/assets/fonts/Economica-Regular-OTF.otf                                          | Bin [31m0[m -> [32m19784[m bytes
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomButton.java            |  23 [32m+++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomEditText.java          |  23 [32m+++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomTextInputEditText.java |  23 [32m+++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/CustomTextView.java          |  23 [32m+++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/CustomViews/TypefaceUtil.java            |  18 [32m++++++++++++++++++[m
 app/src/main/res/drawable/rounded_corner_background.xml                                      |  10 [32m++++++++++[m
 app/src/main/res/drawable/rounded_corner_with_border_background.xml                          |  11 [32m+++++++++++[m
 app/src/main/res/layout/activity_registration.xml                                            |  78 [32m+++++++++++++++++[m[31m-------------------------------------------------------------[m
 app/src/main/res/layout/edit_text_email.xml                                                  |  18 [32m++++++++++++++++++[m
 app/src/main/res/layout/edit_text_password.xml                                               |  22 [32m++++++++++++++++++++++[m
 app/src/main/res/layout/linear_layout_app_name.xml                                           |  19 [32m+++++++++++++++++++[m
 app/src/main/res/values/colors.xml                                                           |   8 [32m+++++[m[31m---[m
 app/src/main/res/values/strings.xml                                                          |   2 [32m++[m
 app/src/main/res/values/styles.xml                                                           |  30 [32m++++++++++++++++++++++++++++++[m
 build.gradle                                                                                 |   2 [32m+[m[31m-[m
 19 files changed, 245 insertions(+), 65 deletions(-)

[33mcommit 37946cf952db37d44df9349321f084e63667e3ca[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed Apr 10 13:55:33 2019 -0400

    Fixed onButtonClick signatures

 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivity.java | 4 [32m++[m[31m--[m
 1 file changed, 2 insertions(+), 2 deletions(-)

[33mcommit 6cf8e37d4f137c2452394ef2435251867ea7d876[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Wed Apr 10 13:41:01 2019 -0400

    Sign in with google seems to be working
    
    Might not be safe to have web client id in strings.
    Added coordinator layout for snackbar.
    Removed firbase cor lib from gradle because it seems to not be necessary.

 app/build.gradle                                                              |  9 [32m+++++[m[31m----[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivity.java | 93 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m---------[m
 app/src/main/res/layout/activity_registration.xml                             |  6 [32m++++[m[31m--[m
 app/src/main/res/values/strings.xml                                           |  3 [32m+++[m
 4 files changed, 96 insertions(+), 15 deletions(-)

[33mcommit bfccd821729170418c38a1c24fd613bbfcce4819[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Fri Apr 5 10:48:49 2019 -0400

    Registration activity WIP
    
    Currently getting runtime error with "Make sure to call FirebaseApp.initializeApp(Context) first."

 app/build.gradle                                                              |  5 [32m+++++[m
 app/src/main/AndroidManifest.xml                                              |  2 [32m++[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivity.java | 48 [32m++++++++++++++++++++++++++++++++++++++++++++++[m[31m--[m
 build.gradle                                                                  |  3 [32m++[m[31m-[m
 4 files changed, 55 insertions(+), 3 deletions(-)

[33mcommit 804ffc1e3efd9e5e049a252133ff98f8d369d824[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Apr 4 20:30:35 2019 -0400

    Login and registration WIP

 app/src/main/java/org/michiganhackers/photoassassin/BindingAdapters.java               |  16 [31m----------------[m
 app/src/main/java/org/michiganhackers/photoassassin/Email.java                         |  29 [32m+++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginActivityViewModel.java        |   7 [31m-------[m
 app/src/main/java/org/michiganhackers/photoassassin/Password.java                      |  14 [32m++++++[m[31m--------[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivity.java          |  45 [32m++++++++++++++++++++++++++++++++++[m[31m-----------[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivityViewModel.java |  20 [31m--------------------[m
 app/src/main/res/layout/activity_login.xml                                             |  16 [32m++++++++[m[31m--------[m
 app/src/main/res/layout/activity_main.xml                                              |   9 [31m---------[m
 app/src/main/res/layout/activity_registration.xml                                      | 157 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m[31m------------------------------------------------------------------------------------[m
 app/src/main/res/values/strings.xml                                                    |   7 [32m+++++++[m
 10 files changed, 157 insertions(+), 163 deletions(-)

[33mcommit 4539651b4d01b8acc14dc1fedb3af4a41b6a5cf2[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Mar 28 19:22:30 2019 -0400

    Testing out data binding. this is a WIP

 app/build.gradle                                                                       |   9 [32m++++++++[m[31m-[m
 app/src/main/AndroidManifest.xml                                                       |  10 [32m+++++++++[m[31m-[m
 app/src/main/java/org/michiganhackers/photoassassin/BindingAdapters.java               |  16 [32m++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginActivity.java                 |  15 [32m+++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/LoginActivityViewModel.java        |   7 [32m+++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/Password.java                      |  33 [32m+++++++++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivity.java          |  27 [32m+++++++++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/RegistrationActivityViewModel.java |  20 [32m++++++++++++++++++++[m
 app/src/main/res/layout/activity_login.xml                                             |  67 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/layout/activity_registration.xml                                      | 100 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/values/strings.xml                                                    |   2 [32m++[m
 11 files changed, 304 insertions(+), 2 deletions(-)

[33mcommit 309983a1775784faf96e059ec648c8d74850bb47[m
Merge: fc836f3 ee838ee
Author: Glavon <owaink2255@gmail.com>
Date:   Fri Feb 22 12:19:50 2019 -0500

    Merge pull request #15 from michiganhackers/readme
    
    Fix to installation instructions "Github" -> "Git"

[33mcommit ee838ee392eb79ddf2414a8a008793d2dc19cd65[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Feb 21 19:53:37 2019 -0500

    Fix to installation instructions "Github" -> "Git"

 README.md | 2 [32m+[m[31m-[m
 1 file changed, 1 insertion(+), 1 deletion(-)

[33mcommit fc836f3bc6aa374004f7b0d162da91f7b839d380[m
Merge: 7b7d3a0 2a914dd
Author: Glavon <owaink2255@gmail.com>
Date:   Mon Feb 11 23:50:33 2019 -0500

    Merge pull request #11 from michiganhackers/readme-installing
    
    Specified where to get jks and keystore.properties

[33mcommit 2a914ddc99a6d224cdad9c4b8f186aa7599e9328[m[33m ([m[1;31morigin/readme-installing[m[33m)[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Fri Feb 8 00:04:58 2019 -0500

    Specified where to get jks and keystore.properties

 README.md | 4 [32m++[m[31m--[m
 1 file changed, 2 insertions(+), 2 deletions(-)

[33mcommit 7b7d3a070ee422cd0413d850c58ebb1f2d5f66fb[m
Merge: 38c0652 ca6aeda
Author: Glavon <owaink2255@gmail.com>
Date:   Thu Feb 7 20:39:47 2019 -0500

    Merge pull request #2 from michiganhackers/new-app-project
    
    New app project

[33mcommit 38c06521c926f5898d1fbd9a9114c53736266214[m
Merge: a35d654 ad1fe3e
Author: Glavon <owaink2255@gmail.com>
Date:   Thu Feb 7 20:39:01 2019 -0500

    Merge pull request #1 from michiganhackers/readme
    
    added link to android knowledge base page

[33mcommit ca6aeda3aa61f4a584aaa6add30bb414070be35a[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jan 26 17:16:32 2019 -0500

    changed keystore fields and added build types
    
    made keystore aliases more specific because we now have 2 android apps.

 app/build.gradle | 15 [32m++++++++++[m[31m-----[m
 1 file changed, 10 insertions(+), 5 deletions(-)

[33mcommit 0fca916f57fcdf13349e7b7cedc9eb3c2d136c93[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jan 26 15:24:49 2019 -0500

    Added keystore properties info to app build.gradle
    
    Also changed readme to say to use same MichiganHackers.jks file as our other app. It will be simpler to just use the same keystore to hold all the keys.

 README.md        |  2 [32m+[m[31m-[m
 app/build.gradle | 18 [32m++++++++++++++++++[m
 2 files changed, 19 insertions(+), 1 deletion(-)

[33mcommit 24641910c4185dcb866c7b8ee8e72b204efa5a35[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Sat Jan 26 14:29:35 2019 -0500

    created a new project in android studio
    
    Chose min sdk version to be 21. Based this decision off the fact that the michigan hackers app had to have a lot of workarounds to support versions less than 21. Also, 21 allows for lock screen notifications, which seems to be something we could end up implementing. According to https://developer.android.com/about/dashboards/, 21+ will support about 90% of people that visit the google play store, and I believe this number is higher for those in the US.

 app/.gitignore                                                                          |   1 [32m+[m
 app/build.gradle                                                                        |  28 [32m++++++++++++++++++++++++++[m
 app/proguard-rules.pro                                                                  |  21 [32m++++++++++++++++++++[m
 app/src/androidTest/java/org/michiganhackers/photoassassin/ExampleInstrumentedTest.java |  27 [32m+++++++++++++++++++++++++[m
 app/src/main/AndroidManifest.xml                                                        |  21 [32m++++++++++++++++++++[m
 app/src/main/java/org/michiganhackers/photoassassin/MainActivity.java                   |  14 [32m+++++++++++++[m
 app/src/main/res/drawable-v24/ic_launcher_foreground.xml                                |  34 [32m++++++++++++++++++++++++++++++++[m
 app/src/main/res/drawable/ic_launcher_background.xml                                    | 170 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 app/src/main/res/layout/activity_main.xml                                               |  18 [32m+++++++++++++++++[m
 app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml                                      |   5 [32m+++++[m
 app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml                                |   5 [32m+++++[m
 app/src/main/res/mipmap-hdpi/ic_launcher.png                                            | Bin [31m0[m -> [32m2963[m bytes
 app/src/main/res/mipmap-hdpi/ic_launcher_round.png                                      | Bin [31m0[m -> [32m4905[m bytes
 app/src/main/res/mipmap-mdpi/ic_launcher.png                                            | Bin [31m0[m -> [32m2060[m bytes
 app/src/main/res/mipmap-mdpi/ic_launcher_round.png                                      | Bin [31m0[m -> [32m2783[m bytes
 app/src/main/res/mipmap-xhdpi/ic_launcher.png                                           | Bin [31m0[m -> [32m4490[m bytes
 app/src/main/res/mipmap-xhdpi/ic_launcher_round.png                                     | Bin [31m0[m -> [32m6895[m bytes
 app/src/main/res/mipmap-xxhdpi/ic_launcher.png                                          | Bin [31m0[m -> [32m6387[m bytes
 app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png                                    | Bin [31m0[m -> [32m10413[m bytes
 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png                                         | Bin [31m0[m -> [32m9128[m bytes
 app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png                                   | Bin [31m0[m -> [32m15132[m bytes
 app/src/main/res/values/colors.xml                                                      |   6 [32m++++++[m
 app/src/main/res/values/strings.xml                                                     |   3 [32m+++[m
 app/src/main/res/values/styles.xml                                                      |  11 [32m+++++++++++[m
 app/src/test/java/org/michiganhackers/photoassassin/ExampleUnitTest.java                |  17 [32m++++++++++++++++[m
 build.gradle                                                                            |  27 [32m+++++++++++++++++++++++++[m
 gradle.properties                                                                       |  20 [32m+++++++++++++++++++[m
 gradle/wrapper/gradle-wrapper.jar                                                       | Bin [31m0[m -> [32m54329[m bytes
 gradle/wrapper/gradle-wrapper.properties                                                |   6 [32m++++++[m
 gradlew                                                                                 | 172 [32m+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 gradlew.bat                                                                             |  84 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 settings.gradle                                                                         |   1 [32m+[m
 32 files changed, 691 insertions(+)

[33mcommit ad1fe3ed8e3e53cc50f9785ba25d53201fb972f8[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Fri Jan 25 00:47:10 2019 -0500

    added link to android knowledge base page

 README.md | 3 [32m++[m[31m-[m
 1 file changed, 2 insertions(+), 1 deletion(-)

[33mcommit a35d654859cf74bf0a80a01be361931eb4be5b6f[m
Author: Vincent Nagel <vnagel@umich.edu>
Date:   Thu Jan 24 23:32:20 2019 -0500

    added gitignore and readme

 .gitignore | 52 [32m++++++++++++++++++++++++++++++++++++++++++++++++++++[m
 README.md  | 32 [32m++++++++++++++++++++++++++++++++[m
 2 files changed, 84 insertions(+)
