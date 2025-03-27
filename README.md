# VocaBuilder

VocaBuilder is an Android application designed to help users improve their vocabulary through an infinite scroll of words. Each screen presents a word with its meaning, usage examples, synonyms, and antonyms.

## Features

- Vertical infinite scroll of vocabulary words
- Detailed view for each word including:
  - Word prominently displayed
  - Definitions with parts of speech
  - Example usage in sentences
  - Synonyms and antonyms
- Clean, modern UI with card-based design
- Efficient word data loading with pagination
- Integration with Dictionary API for word data

## Technical Details

- Built with Java for Android
- Uses ViewPager2 for smooth vertical scrolling
- OkHttp for API communication
- Free Dictionary API integration (dictionaryapi.dev)
- CardView and Nested ScrollView for word presentation

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Build and run the application on your device or emulator

## API Information

The application uses the free Dictionary API (https://dictionaryapi.dev/) to fetch word details. This API doesn't require an API key and provides comprehensive word information including:

- Definitions
- Parts of speech
- Example sentences
- Synonyms
- Antonyms

## Future Enhancements

- Word bookmarking functionality
- Word of the day notifications
- Quiz based on viewed words
- Offline mode with cached words
- User-customizable themes
- Pronunciation audio

## License

See the LICENSE file for details.