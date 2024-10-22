### TheDeltaScannerV2

**About the Project:**

TheDeltaScannerV2 is an Android app designed to showcase the capabilities of barcode and RFID scanning devices. Offering various scanning modes and a plethora of customizable settings, our app strikes a perfect balance between simplicity and functionality. The intuitive GUI display ensures swift navigation, making scanning tasks a breeze.

**Key Features:**
- **Versatile Scanning Modes:** Choose from a range of scanning modes to suit your needs, from barcode to RFID.
- **Customizable Settings:** Tailor the app to your preferences with numerous setting options for an optimized scanning experience.
- **Intuitive GUI:** Enjoy a user-friendly interface that facilitates quick and effortless navigation.
- **Enhanced Compatibility:** Seamlessly integrate with Zebra or Honeywell Android devices for smooth operation.
- - **Integrated Camera Scanning Software:** Quickly scan barcodes on any device with just a camera.

**New Features (Coming Soon Possibly):**
- **Real-time Analytics:** Track scanning performance and generate detailed analytics reports for better insights.
- **Batch Scanning:** Streamline operations by scanning multiple items at once, increasing efficiency.
- **Cloud Sync:** Synchronize scanned data with cloud storage for seamless access and backup.
- **Customizable Themes:** Personalize the app's appearance with a selection of themes to match your style.
- **Gesture Controls:** Navigate the app with ease using intuitive gesture controls for a smoother user experience.

**Steps for Use:**

1. **Download Android Studio:** Obtain Android Studio for app development.
2. **Open Project:** Navigate to Version Control and enter the repository URL to access the project.
3. **Configure SDK:** In Gradle scripts `local.properties`, update sdk.dir to your SDK file path.
4. **Build .Gradle:** Click the hammer to build the android environment.
5. **Add Configurations:** Go to the top bar, select "Add Configurations," then choose "Android app" and specify the module `TheDeltaScannerV2.app.main`.
   ![Example](./media/configuration.png)
6. **Deploy APK:** Select "Deploy (APK from app Bundle)" and click OK to deploy the app.
7. **ReBuild and Run:** Build the project and run it on Zebra or Honeywell Android devices.
8. **Additional Step for Zebra Devices:** Ensure you have downloaded and installed the DataWedge app for Zebra devices.
9. **Add API Key for Swift Scanner:** Create `secrets.properties` and add the value: `SWIFT_DECODER_API_KEY="your api key"`

Explore the enhanced features and elevate your understanding with TheDeltaScannerV2!
