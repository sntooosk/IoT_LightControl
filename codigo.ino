#include <WiFi.h>
#include <FirebaseESP32.h>

#define WIFI_SSID "ETEC"
#define WIFI_PASSWORD "etec@147"

#define API_KEY "AIzaSyAQV8VWiYifwjjBKlXK4f8VGA6M3OYS2ms"
#define USER_EMAIL "Juliano.santos88@icloud.com"
#define USER_PASSWORD "Juli@no7365"
#define DATABASE_URL "https://lampada-iot-7eb44-default-rtdb.firebaseio.com/"

#define LED_PIN 15
#define STATUS_LED_PIN 2

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  pinMode(STATUS_LED_PIN, OUTPUT); 
  Serial.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());

  // Apaga o LED de status azul após a conexão
  digitalWrite(STATUS_LED_PIN, LOW);

  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  if (Firebase.getString(fbdo, "/lampada/status")) {
    String ledState = fbdo.stringData();
    
    if (ledState == "ON") {
      digitalWrite(LED_PIN, HIGH);
    } else if (ledState == "OFF") {
      digitalWrite(LED_PIN, LOW);
    }
  } else {
    Serial.println("Error reading from Firebase");
    Serial.println(fbdo.errorReason());
  }

  delay(1000);
}
