package com.example.data

import androidx.compose.ui.graphics.Color

// Represents a dubbing language
enum class DubLanguage(val displayName: String, val code: String, val bhasha: String) {
    BENGALI("বাংলা (Bengali)", "bn", "বাংলা"),
    HINDI("हिन्दी (Hindi)", "hi", "হিন্দি"),
    ENGLISH("English", "en", "ইংরেজি")
}

// Represents emotional voice expressions for true-to-life human range
enum class VoiceEmotion(val displayName: String, val bengaliName: String, val promptModifier: String, val colorHex: Long) {
    NEUTRAL("Natural/Calm", "স্বাভাবিক ও শান্ত", "With a calm, natural, conversational voice tone.", 0xFF9E9E9E),
    HAPPY("Joyful/Happy", "উচ্ছ্বসিত ও খুশি", "With standard joyful laughter, smiling undertone, and bright expressions.", 0xFF4CAF50),
    ANGRY("Fierce/Angry", "রাগান্বিত ও তীব্র", "With high-intensity breathy anger, screaming grit, and fierce vocal texture.", 0xFFF44336),
    SAD("Crying/Sad", "আবেগময় ও বিষণ্ণ", "With heavy tearful pauses, emotional trembling, and low melancholy dynamic range.", 0xFF2196F3),
    EXCITED("Shouting/Excited", "রোমাঞ্চিত ও উত্তেজিত", "With hyper-fast energetic pace, high pitch, and absolute extreme vigor.", 0xFFFF9800)
}

// Represents a synthetic real-human voice clone configuration
data class VoiceProfile(
    val id: String,
    val name: String,
    val description: String,
    val gender: String,
    val basePitch: Float, // 0.5 to 2.0
    val resonance: Float, // 0.0 to 1.0 (human breathy airiness vs deep chest)
    val warmth: Float, // 0.0 to 1.0 (natural humanness)
    val isCloned: Boolean = false,
    val audioUrlSimulated: String = "",
    val color: Color = Color(0xFF00E5FF),
    val assistantRole: String = "ডাবিং ও লিপ-সিঙ্ক সহযোগী",
    val assistantStatus: String = "সক্রিয় (Active)",
    val assistantInstructions: String = "ভাবানুবাদের সাথে সংলাপে আবেগ বজায় রাখো।"
)

// Represents dialog subtitle lines mapping timing, original text, and user translations
data class SubtitleCue(
    val id: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val characterName: String,
    val originalText: String,
    val bengaliTranslation: String,
    val hindiTranslation: String,
    val englishTranslation: String,
    var generatedVocalFile: String = "", // Simulator path
    var emotion: VoiceEmotion = VoiceEmotion.NEUTRAL,
    var lipSyncDone: Boolean = false,
    var mouthAccuracyMultiplier: Float = 1.0f
) {
    // Return translation based on selected language
    fun getTextForLanguage(lang: DubLanguage): String {
        return when (lang) {
            DubLanguage.BENGALI -> bengaliTranslation.ifEmpty { "অনুবাদ করা হচ্ছে..." }
            DubLanguage.HINDI -> hindiTranslation.ifEmpty { "अनुवाद किया जा रहा है..." }
            DubLanguage.ENGLISH -> englishTranslation.ifEmpty { originalText }
        }
    }
}

// Represents an Anime Movie Project
data class AnimeProject(
    val id: String,
    val title: String,
    val genre: String,
    val durationSeconds: Int,
    val imageUrl: String,
    val originalLanguage: String,
    val description: String,
    val bhashaDescription: String,
    val characters: List<VoiceProfile>,
    val subtitles: List<SubtitleCue>
)

// Holds all parameters for the Lip Sync engine config
data class LipSyncConfig(
    val accuracyMultiplier: Float = 0.98f, // 98% default
    val voiceFrameOffsetMs: Float = 0.0f,     // 0ms delay offset
    val facialMeshSensitivity: Float = 0.85f, // Real-time mesh alignment
    val autoBreathPauses: Boolean = true,     // Inject biological pauses
    val phonemeLipWarpStrength: Float = 0.90f // Shape of cartoon lips matches voice
)

// Mock Data Source for the App
object MockProjects {
    val predefinedProfiles = listOf(
        // Demon Slayer Profiles
        VoiceProfile("tanjiro", "Tanjiro Kamado (তানজিরো)", "যুবক বীর যোদ্ধা - দৃঢ় ও আবেগপূর্ণ কণ্ঠ", "Male", 1.0f, 0.82f, 0.95f, false, "", Color(0xFF22C55E)),
        VoiceProfile("nezuko", "Nezuko Kamado (নেজুকো)", "মিষ্টি কিশোরী - গুণগুণ ও শান্ত মিষ্টি কণ্ঠ", "Female", 1.4f, 0.75f, 0.98f, false, "", Color(0xFFEC4899)),
        
        // Naruto Profiles
        VoiceProfile("naruto", "Naruto Uzumaki (নারুটো)", "উত্তেজিত ও উচ্চকণ্ঠী ডানপিটে স্পিরিট", "Male", 1.1f, 0.60f, 0.80f, false, "", Color(0xFFF97316)),
        VoiceProfile("sasuke", "Sasuke Uchiha (সাসুকে)", "গম্ভীর, রাগী ও ঠান্ডা ঠোঁটকাটা কণ্ঠস্বর", "Male", 0.85f, 0.90f, 0.70f, false, "", Color(0xFF6366F1)),
        
        // Your Name Profiles
        VoiceProfile("taki", "Taki Tachibana (তাকি)", "টোকিওর সাধারণ ছাত্র - সাবলীল ও প্রাণবন্ত কণ্ঠ", "Male", 0.95f, 0.78f, 0.90f, false, "", Color(0xFF3B82F6)),
        VoiceProfile("mitsuha", "Mitsuha Miyamizu (মিতসুহা)", "পল্লী অঞ্চলের সহজ-সরল আবেগময়ী মেয়ে", "Female", 1.3f, 0.80f, 0.96f, false, "", Color(0xFFEAB308))
    )

    val animeProjects = listOf(
        AnimeProject(
            id = "proj_demon_slayer",
            title = "Demon Slayer: Mugen Train (ডেমন স্লেয়ার)",
            genre = "Action / Fantasy",
            durationSeconds = 12,
            imageUrl = "demon_slayer",
            originalLanguage = "Japanese",
            description = "Tanjiro fights with complete devotion to save his sister Nezuko and protect the innocent travelers.",
            bhashaDescription = "তানজিরো তার বোন নেজুকোকে বাঁচাতে এবং নিরীহ ট্রেন যাত্রীদের রক্ষা করতে নিজের জীবন বাজি রেখে যুদ্ধ করছে।",
            characters = listOf(predefinedProfiles[0], predefinedProfiles[1]),
            subtitles = listOf(
                SubtitleCue(
                    id = "ds_cue_1",
                    startTimeMs = 500,
                    endTimeMs = 3500,
                    characterName = "Tanjiro",
                    originalText = "No matter what, I will turn you back into a human, Nezuko!",
                    bengaliTranslation = "যাই ঘটুক না কেন নেজুকো, আমি তোমাকে আবারও মানুষ বানাবো! আমি প্রতিজ্ঞা করছি!",
                    hindiTranslation = "चाहे कुछ भी हो जाए नेज़ुको, मैं तुम्हें फिर से इंसान बनाकर रहूँगा!",
                    englishTranslation = "No matter what happens, Nezuko, I will turn you back into a human!"
                ),
                SubtitleCue(
                    id = "ds_cue_2",
                    startTimeMs = 4000,
                    endTimeMs = 6000,
                    characterName = "Nezuko",
                    originalText = "Mmh... Mmmph! (Growling gently and nodding)",
                    bengaliTranslation = "উমম... উম্মফ! (ধীরে মাথা নাড়িয়ে সায় দিল)",
                    hindiTranslation = "उम्म... उम्मफ! (धीरे से हाँ में सिर हिलाया)",
                    englishTranslation = "Mmh... Mmmph! (Nodding gently in determination)"
                ),
                SubtitleCue(
                    id = "ds_cue_3",
                    startTimeMs = 6500,
                    endTimeMs = 11500,
                    characterName = "Tanjiro",
                    originalText = "Hinokami Kagura! Flame Dance! Consume everything before us!",
                    bengaliTranslation = "হিনোকামি কাগুরা! অগ্নিনৃত্য! আমাদের সামনের সমস্ত অন্ধকার পুড়িয়ে ছারখার করে দাও!",
                    hindiTranslation = "हिनोकामी कागूरा! अग्नि नृत्य (फ्लेम डांस)! हमारे सामने के सब अंधकार को नष्ट कर दो!",
                    englishTranslation = "Hinokami Kagura! Flame Dance! Set my heart ablaze and burn everything!"
                )
            )
        ),
        AnimeProject(
            id = "proj_naruto",
            title = "Naruto Shippuden: Eternal Bond (নারুটো)",
            genre = "Ninjutsu / Shonen",
            durationSeconds = 15,
            imageUrl = "naruto_shippuden",
            originalLanguage = "Japanese",
            description = "The fateful duel, Naruto attempts to snap Sasuke out of hatred with raw emotion.",
            bhashaDescription = "চূড়ান্ত সেই যুদ্ধক্ষেত্রে, নারুটো তার সমস্ত অনুভূতি উজার করে সাসুকার মনের ঘৃণা দূর করার চেষ্টা করছে।",
            characters = listOf(predefinedProfiles[2], predefinedProfiles[3]),
            subtitles = listOf(
                SubtitleCue(
                    id = "na_cue_1",
                    startTimeMs = 800,
                    endTimeMs = 4500,
                    characterName = "Naruto",
                    originalText = "Sasuke! Do you really think cutting our bond is that easy? I won't let you!",
                    bengaliTranslation = "সাসুকে! আমাদের বন্ধন ছিন্ন করা কি এতই সহজ মনে করিস? আমি তা কিছুতেই হতে দেব না!",
                    hindiTranslation = "सासूके! क्या तुम्हें सच में लगता है कि हमारा रिश्ता तोड़ना इतना आसान है? मैं ऐसा नहीं होने दूँगा!",
                    englishTranslation = "Sasuke! Do you really think breaking our bond is that simple? I will never let you!"
                ),
                SubtitleCue(
                    id = "na_cue_2",
                    startTimeMs = 5000,
                    endTimeMs = 9000,
                    characterName = "Sasuke",
                    originalText = "Shut up, Naruto... You don't know the depth of my darkness. This bond only makes me weak.",
                    bengaliTranslation = "চুপ করিস নারুটো... তুই আমার মনের গভীর অন্ধকার বুঝবি না। এই বন্ধুত্ব কেবল আমাকে দুর্বল করে।",
                    hindiTranslation = "चुप हो जाओ, नारुतो... तुम मेरी अंधकार की गहराई नहीं जानते। यह रिश्ता मुझे सिर्फ कमजोर बनाता है।",
                    englishTranslation = "Shut up, Naruto... You don't understand my deep darkness. These bonds only drag me down."
                ),
                SubtitleCue(
                    id = "na_cue_3",
                    startTimeMs = 9500,
                    endTimeMs = 14500,
                    characterName = "Naruto",
                    originalText = "I don't care about weakness! If you fall into darkness, I'll drag you out of there!",
                    bengaliTranslation = "আমি দুর্বলতার তোয়াক্কা করি না! তুই যদি নরকেও চলে যাস, আমি সেখান থেকেও টেনে বের করে আনবো!",
                    hindiTranslation = "मुझे कमजोरी की कोई परवाह नहीं! अगर तुम अंधेरे में गिरते हो, तो मैं तुम्हें खींचकर बाहर लाऊंगा!",
                    englishTranslation = "I don't care about weakness! If you plunge into darkness, I'll physically drag you back!"
                )
            )
        ),
        AnimeProject(
            id = "proj_your_name",
            title = "Your Name: Kataware-doki (তোমার নাম)",
            genre = "Romance / Sci-Fi",
            durationSeconds = 14,
            imageUrl = "your_name",
            originalLanguage = "Japanese",
            description = "Taki and Mitsuha finally meet across time at sunset.",
            bhashaDescription = "সূর্যাস্তের মায়াবী আলোয় সময়ের ব্যবধান ঘুচিয়ে তাকি এবং মিতসুহা অবশেষে একে অপরের মুখোমুখি হলো।",
            characters = listOf(predefinedProfiles[4], predefinedProfiles[5]),
            subtitles = listOf(
                SubtitleCue(
                    id = "yn_cue_1",
                    startTimeMs = 600,
                    endTimeMs = 4600,
                    characterName = "Taki",
                    originalText = "I came to find you... It wasn't easy since you were so far. Who are you?",
                    bengaliTranslation = "আমি তোমাকে খুঁজতে এসেছিলাম... তুমি এতটা দূরে ছিলে যে কাজটা সহজ ছিল না। তোমার নাম কী?",
                    hindiTranslation = "मैं तुम्हें ढूंढने आया था... तुम इतनी दूर थीं कि यह आसान नहीं था। तुम्हारा नाम क्या है?",
                    englishTranslation = "I crossed timelines to find you... It was so hard since you were so far away. What is your name?"
                ),
                SubtitleCue(
                    id = "yn_cue_2",
                    startTimeMs = 5000,
                    endTimeMs = 8500,
                    characterName = "Mitsuha",
                    originalText = "Taki-kun! I remember... I saw your face in my dreams. Don't let me forget!",
                    bengaliTranslation = "তাকি-কুন! আমার মনে পড়েছে... আমি স্বপ্নে তোমার মুখ দেখেছিলাম। আমাকে ভুলে যেতে দিও না!",
                    hindiTranslation = "ताकी-कुन! मुझे याद है... मैंने सपनों में तुम्हारा चेहरा देखा था। मुझे भूलने मत देना!",
                    englishTranslation = "Taki-kun! I remember... I saw you in my recurring dreams. Please don't let me forget!"
                ),
                SubtitleCue(
                    id = "yn_cue_3",
                    startTimeMs = 9000,
                    endTimeMs = 13500,
                    characterName = "Taki",
                    originalText = "Let's write our names on each other's hands... that way we'll never forget.",
                    bengaliTranslation = "আসো আমরা একে অপরের হাতের তালুতে নিজের নাম লিখে রাখি... যাতে কখনো ভুলে না যাই।",
                    hindiTranslation = "चलो एक-दूसरे के हाथ पर अपना नाम लिख लें... ताकि हम कभी न भूलें।",
                    englishTranslation = "Let's write our names on each other's palms... so that we will never lose each other."
                )
            )
        )
    )
}
