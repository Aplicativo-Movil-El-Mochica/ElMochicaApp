# ElMochicaApp 📱

<div align="center">
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/kotlin/kotlin-original.svg" alt="Kotlin" width="60" height="60"/>
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/android/android-original.svg" alt="Android" width="60" height="60"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/6/6b/Gradle_logo.svg" alt="Gradle" width="60" height="60"/>
  <img src="https://developer.android.com/static/studio/images/studio-icon.svg" alt="Android Studio" width="60" height="60"/>
</div>

<br>

ElMochicaApp es una aplicación móvil para la gestión de pedidos de un restaurante, optimizando la atención al cliente más allá del uso de WhatsApp o llamadas.

## 🎯 Características Principales

- **📱 Nativo Android**: Desarrollo Kotlin para máximo rendimiento
- **🎨 Diseño Moderno**: Interfaz de usuario intuitiva siguiendo Material Design
- **🔒 Seguridad**: Implementación de mejores prácticas de seguridad móvil
- **🌐 Conectividad**: Integración con APIs y servicios backend

## 🚀 Tecnologías

### Desarrollo Móvil
- **Kotlin** - Lenguaje de programación principal
- **Android SDK** - Kit de desarrollo para Android
- **Android Studio** - IDE oficial de desarrollo
- **Gradle** - Sistema de construcción y gestión de dependencias

### Arquitectura
- **MVVM** - Patrón Model-View-ViewModel
- **Android Jetpack** - Conjunto de bibliotecas modernas
- **Material Design** - Guías de diseño de Google

## 📋 Prerrequisitos

- **Android Studio** Arctic Fox o superior
- **JDK 11** o superior
- **Android SDK** (API nivel 21 o superior)
- **Kotlin** 1.8 o superior
- **Gradle** 7.0 o superior

## 🛠️ Instalación

### Configuración del Entorno

```bash
# Clonar el repositorio
git clone https://github.com/Aplicativo-Movil-El-Mochica/ElMochicaApp.git

# Navegar al directorio del proyecto
cd ElMochicaApp

# Abrir en Android Studio
# File > Open > Seleccionar la carpeta del proyecto
```

### Compilación y Ejecución

```bash
# Compilar el proyecto
./gradlew build

# Ejecutar tests
./gradlew test

# Generar APK de debug
./gradlew assembleDebug

# Generar APK de release
./gradlew assembleRelease
```

## 🚀 Deployment

### APK de Debug
```bash
./gradlew assembleDebug
# APK generado en: app/build/outputs/apk/debug/
```

### APK de Release
```bash
./gradlew assembleRelease
# APK generado en: app/build/outputs/apk/release/
```

## 📱 Compatibilidad

- **Android Mínimo**: API 21 (Android 5.0 Lollipop)
- **Android Target**: API 34 (Android 14)
- **Arquitecturas**: ARM64, ARMv7, x86, x86_64

## 🎨 Características de UI/UX

- **Material Design 3** - Diseño moderno y accesible
- **Tema Dinámico** - Soporte para colores del sistema
- **Modo Oscuro** - Tema claro/oscuro automático
- **Animaciones Fluidas** - Transiciones suaves entre pantallas
- **Accesibilidad** - Soporte completo para usuarios con discapacidades

## 🔧 Configuración de Desarrollo

### Variables de Entorno
```kotlin
// En build.gradle (app)
android {
    buildTypes {
        debug {
            buildConfigField "String", "API_URL", "\"https://api-dev.elmochica.com\""
        }
        release {
            buildConfigField "String", "API_URL", "\"https://api.elmochica.com\""
        }
    }
}
```
<div align="center">
  <br><br>
  <i>Desarrollado con ❤️ por Felines</i>
</div>
