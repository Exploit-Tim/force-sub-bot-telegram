# 🔒 Force Subscribe Telegram Bot (Java + Spring Boot)

Bot Telegram berbasis Java Spring Boot yang mewajibkan pengguna untuk bergabung ke channel sebelum mengakses konten atau
fitur tertentu. Cocok untuk admin channel yang ingin meningkatkan jumlah subscriber secara otomatis.

---

## 🚀 Fitur Utama

- ✅ **Force Subscribe**: Wajib join ke satu atau lebih channel.
- 🔘 **Tombol Join Otomatis**: Inline button dengan link langsung.
- 💬 **Pesan Dinamis**: Gunakan placeholder seperti `{username}`, `{botusername}`, dll.
- 📩 **Resend Pesan via Payload**: Gunakan `/start <id>` untuk forward pesan dari channel lain.
- 📊 **Logging Channel yang Belum Diikuti**.
- ⚙️ **Modular dan Asinkron**: Menggunakan Spring Boot dan TelegramBots API, dan dapat menambah Button sesukanya.

---

## 📦 Teknologi

- Java 17+
- Spring Boot 3.x
- Maven
- TelegramBots (rubenlagus)
- MongoDB

---

## 🛠️ Instalasi & Konfigurasi

### 1. Install Java & Maven

#### Ubuntu/Debian

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven -y
```

#### macOS (Homebrew)

```bash
brew install openjdk@17 maven
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

#### Windows

- Unduh JDK 17 dari: https://adoptium.net/
- Unduh Maven dari: https://maven.apache.org/download.cgi
- Tambahkan ke `PATH` di Environment Variables

Verifikasi instalasi:

```bash
java -version
mvn -version
```

---

### 2. Clone Repository

```bash
git clone https://github.com/piyandra/force-sub-bot-telegram.git
cd force-sub-bot-telegram
```

---

### 3. Konfigurasi `application.properties`

Buat file `src/main/resources/application.properties` dengan isi berikut:

```properties
# Telegram Bot Config
spring.application.name=Telegram Bot Force Sub
# MongoDB URI with improved configuration
spring.data.mongodb.uri=
# Connection settings
spring.data.mongodb.connection-pool-max-wait-time=20000
spring.data.mongodb.connect-timeout=10000
spring.data.mongodb.socket-timeout=20000
spring.data.mongodb.max-connection-idle-time=60000
spring.data.mongodb.retry-writes=true
# MongoDB connection pool settings
spring.data.mongodb.connection-pool-min-size=5
spring.data.mongodb.connection-pool-max-size=10
bot.token=
bot.username=
owner.username=
owner.userid=
channel.id=
bot.id=
data.message=
start.message.not.join=
start.message.after.join=
help.message=

```

---

### 4. Setup Database

Gunakan MongoDB untuk fleksibilitas

---

### 5. Build & Jalankan

```bash
mvn clean install
java -jar target/force-sub-bot-telegram.jar
```

---

## ⚙️ Penggunaan

- Kirim `/start` ke bot → menerima pesan bantuan
- Kirim `/start <id>` → bot akan salin pesan berdasarkan ID
- Jika belum join semua channel → muncul tombol join
- Setelah join → dapat akses pesan/konten

---

## 🔧 Placeholder Pesan

| Placeholder       | Deskripsi                            |
|-------------------|--------------------------------------|
| `{username}`      | Username Telegram user               |
| `{firstname}`     | Nama depan user                      |
| `{lastname}`      | Nama belakang user                   |
| `{userid}`        | ID Telegram user                     |
| `{botusername}`   | Username bot                         |
| `{ownerusername}` | Username owner (dalam bentuk tautan) |

Gunakan dalam application.properties

---

## 📁 Struktur Direktori (Ringkasan)

```
.
├── entity/               # Entitas database
├── handler/              # Command handler bot
├── service/              # Logika bisnis utama
├── utils/                # Tombol & helper lainnya
└── ForceSubBotApplication.java
```

---

## 🤝 Kontribusi

Pull request dan issue sangat diterima. Jika kamu ingin menambahkan fitur atau menemukan bug, silakan fork dan kirim PR.

---

## 📜 Lisensi

MIT License © [piyandra](https://github.com/piyandra)

---

## 📬 Kontak

Untuk pertanyaan, silakan kirim DM ke [@piyandra](https://t.me/piyandra)
