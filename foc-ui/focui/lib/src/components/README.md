# JSON Form Builder Component

A powerful Flutter component that parses JSON layout definitions and renders FlutterFormBuilder forms dynamically.

## Features

- 🎨 **Dynamic Form Generation**: Create forms from JSON configuration
- 📱 **Multiple Input Types**: Text, number, email, password, dropdown, radio, checkbox, date, slider, and more
- ✅ **Built-in Validation**: Support for required fields, min/max length, patterns, and custom validators
- 🎛️ **Flexible Layouts**: Column, row, grid, and wrap layouts
- 🔧 **Customizable**: Styling, spacing, sections, and conditional fields
- 📁 **Multiple Data Sources**: Load from assets, JSON string, or direct data
- 🐛 **Debug Support**: Optional debug logging for development

## Usage

### Basic Usage

```dart
import 'package:flutter/material.dart';
import 'json_form_builder.dart';

class MyForm extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: JsonFormBuilder(
        assetPath: 'assets/forms/my_form.json',
        onChanged: (values) {
          print('Form values changed: $values');
        },
        onSaved: (values) {
          print('Form saved: $values');
        },
      ),
    );
  }
}
```

### Advanced Usage with Form Control

```dart
class AdvancedForm extends StatefulWidget {
  @override
  _AdvancedFormState createState() => _AdvancedFormState();
}

class _AdvancedFormState extends State<AdvancedForm> {
  final GlobalKey<FormBuilderState> _formKey = GlobalKey<FormBuilderState>();

  @override
  Widget build(BuildContext context) {
    return JsonFormBuilder(
      formKey: _formKey,
      assetPath: 'assets/forms/sample_form.json',
      autovalidateMode: AutovalidateMode.onUserInteraction,
      initialValues: {
        'name': 'John Doe',
        'email': 'john@example.com',
      },
      onChanged: (values) {
        // Handle form changes
      },
    );
  }

  void saveForm() {
    if (_formKey.currentState?.saveAndValidate() == true) {
      final values = _formKey.currentState!.value;
      // Process form data
    }
  }
}
```

## JSON Configuration Format

### Basic Form Structure

```json
{
  "title": "My Form",
  "layout": "column",
  "spacing": 16,
  "fields": [
    // Field definitions here
  ]
}
```

### Supported Field Types

#### Text Input
```json
{
  "type": "text",
  "name": "username",
  "label": "Username",
  "hint": "Enter your username",
  "required": true,
  "maxLines": 1,
  "keyboardType": "text",
  "validators": [
    {
      "type": "min_length",
      "value": 3
    }
  ]
}
```

#### Number Input
```json
{
  "type": "number",
  "name": "age",
  "label": "Age",
  "required": true,
  "validators": [
    {
      "type": "min",
      "value": 18
    },
    {
      "type": "max",
      "value": 120
    }
  ]
}
```

#### Email Input
```json
{
  "type": "email",
  "name": "email",
  "label": "Email Address",
  "required": true
}
```

#### Password Input
```json
{
  "type": "password",
  "name": "password",
  "label": "Password",
  "required": true,
  "validators": [
    {
      "type": "min_length",
      "value": 8
    }
  ]
}
```

#### Dropdown
```json
{
  "type": "dropdown",
  "name": "country",
  "label": "Country",
  "required": true,
  "options": [
    {
      "value": "us",
      "label": "United States"
    },
    {
      "value": "ca",
      "label": "Canada"
    }
  ]
}
```

#### Radio Buttons
```json
{
  "type": "radio",
  "name": "gender",
  "label": "Gender",
  "options": [
    {
      "value": "male",
      "label": "Male"
    },
    {
      "value": "female",
      "label": "Female"
    }
  ]
}
```

#### Checkbox Group
```json
{
  "type": "checkbox_group",
  "name": "interests",
  "label": "Interests",
  "options": [
    {
      "value": "technology",
      "label": "Technology"
    },
    {
      "value": "sports",
      "label": "Sports"
    }
  ]
}
```

#### Single Checkbox
```json
{
  "type": "checkbox",
  "name": "newsletter",
  "label": "Subscribe to newsletter"
}
```

#### Switch
```json
{
  "type": "switch",
  "name": "notifications",
  "label": "Enable notifications"
}
```

#### Date Picker
```json
{
  "type": "date",
  "name": "birthDate",
  "label": "Birth Date",
  "format": "yyyy-MM-dd"
}
```

#### Time Picker
```json
{
  "type": "time",
  "name": "appointmentTime",
  "label": "Appointment Time"
}
```

#### Date & Time Picker
```json
{
  "type": "datetime",
  "name": "eventDateTime",
  "label": "Event Date & Time"
}
```

#### Slider
```json
{
  "type": "slider",
  "name": "experience",
  "label": "Years of Experience",
  "min": 0,
  "max": 50,
  "divisions": 50
}
```

#### Range Slider
```json
{
  "type": "range_slider",
  "name": "priceRange",
  "label": "Price Range",
  "min": 0,
  "max": 1000,
  "divisions": 20
}
```

#### File Picker
```json
{
  "type": "file",
  "name": "resume",
  "label": "Upload Resume",
  "allowMultiple": false,
  "allowedExtensions": ["pdf", "doc", "docx"]
}
```

### Layout and Structure

#### Sections
```json
{
  "type": "section",
  "title": "Personal Information",
  "fields": [
    // Fields within this section
  ]
}
```

#### Spacer
```json
{
  "type": "spacer",
  "height": 20
}
```

#### Divider
```json
{
  "type": "divider"
}
```

#### Text Widget
```json
{
  "type": "text_widget",
  "text": "Please fill out all required fields",
  "style": {
    "fontSize": 14,
    "color": "#666666",
    "fontWeight": "normal"
  }
}
```

### Layout Options

#### Column Layout (Default)
```json
{
  "layout": "column",
  "spacing": 16
}
```

#### Row Layout
```json
{
  "layout": "row",
  "spacing": 16
}
```

#### Grid Layout
```json
{
  "layout": "grid",
  "crossAxisCount": 2,
  "spacing": 16
}
```

#### Wrap Layout
```json
{
  "layout": "wrap",
  "spacing": 16
}
```

### Validation

#### Built-in Validators
```json
{
  "validators": [
    {
      "type": "min_length",
      "value": 3
    },
    {
      "type": "max_length",
      "value": 50
    },
    {
      "type": "min",
      "value": 18
    },
    {
      "type": "max",
      "value": 120
    },
    {
      "type": "pattern",
      "value": "^[a-zA-Z0-9]+$"
    },
    {
      "type": "custom",
      "pattern": "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
      "message": "Password must contain at least 8 characters with letters and numbers"
    }
  ]
}
```

## Component Properties

| Property | Type | Description |
|----------|------|-------------|
| `formData` | `Map<String, dynamic>?` | Direct form configuration data |
| `jsonString` | `String?` | JSON configuration as string |
| `assetPath` | `String?` | Path to JSON asset file |
| `initialValues` | `Map<String, dynamic>?` | Initial form values |
| `onChanged` | `Function(Map<String, dynamic>?)?` | Called when form values change |
| `onSaved` | `Function(Map<String, dynamic>?)?` | Called when form is saved |
| `autovalidateMode` | `AutovalidateMode` | Form validation mode |
| `formKey` | `GlobalKey<FormBuilderState>?` | Custom form key |
| `enableDebug` | `bool` | Enable debug logging |

## Methods

Access these methods through the form key:

```dart
final formKey = GlobalKey<FormBuilderState>();

// Save and validate form
bool isValid = formKey.currentState?.saveAndValidate() ?? false;

// Get current values
Map<String, dynamic>? values = formKey.currentState?.value;

// Validate form
bool isValid = formKey.currentState?.validate() ?? false;

// Reset form
formKey.currentState?.reset();
```

## Complete Example

See `json_form_builder_example.dart` for a complete working example with:
- Loading forms from assets
- Form validation
- Real-time value updates
- Save/reset functionality
- Error handling

## Dependencies

This component requires the following packages:
- `flutter_form_builder: ^10.0.1`
- `form_builder_validators: ^8.0.0`

These are already included in your `pubspec.yaml`.
