package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout.LayoutParams
import org.json.JSONObject
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Message(
    val sender: String,
    val receiver: String,
    val timestamp: String,
    val content: String
)
class MainActivity : AppCompatActivity() {

    private lateinit var layout: LinearLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "UserPrefs"
    private val USERS_KEY = "users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //resetAllData()
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setPadding(50, 100, 50, 100)
        }


        showInitialMenu()
        setContentView(layout)
    }
    private fun resetAllData() {
        getSharedPreferences("messages", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("users", MODE_PRIVATE).edit().clear().apply()
    }

    private fun showInitialMenu() {
        layout.removeAllViews()

        val loginButton = Button(this).apply {
            text = "Login"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val registerButton = Button(this).apply {
            text = "Register"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val exitButton = Button(this).apply {
            text = "Exit"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        layout.addView(loginButton)
        layout.addView(registerButton)
        layout.addView(exitButton)
        loginButton.setOnClickListener { showLoginUI() }
        registerButton.setOnClickListener { showRegisterUI() }
        exitButton.setOnClickListener {
            finish()
        }
    }
    private fun markConversationAsRead(currentUser: String, otherUser: String) {
        val prefs = getSharedPreferences("chat_read_status", MODE_PRIVATE)
        val editor = prefs.edit()

        val messages = getChatMessages(currentUser, otherUser).split("\n")
        val lastMessageTimestamp = messages.lastOrNull()?.split("|")?.getOrNull(1)

        if (lastMessageTimestamp != null) {
            editor.putString("$currentUser-$otherUser", lastMessageTimestamp)
            editor.apply()
        }
    }
    private fun showLoginUI() {
        layout.removeAllViews()
        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        Log.d("Login", "Loaded users: $usersJson")

        emailEditText = EditText(this).apply {
            hint = "Email"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordEditText = EditText(this).apply {
            hint = "Password"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        val loginButton = Button(this).apply {
            text = "Login"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showInitialMenu()
        }


        layout.addView(emailEditText)
        layout.addView(passwordEditText)
        layout.addView(loginButton)
        layout.addView(backButton)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
            val users = JSONObject(usersJson)

            if (users.has(email) && users.getString(email) == password) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                showHomePage(email)
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegisterUI() {
        layout.removeAllViews()
        emailEditText = EditText(this).apply {
            hint = "Email"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordEditText = EditText(this).apply {
            hint = "Password"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val registerButton = Button(this).apply {
            text = "Register"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        backButton.setOnClickListener {
            showInitialMenu()
        }
       val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setPadding(16, 32, 16, 16)
        }
        inputLayout.addView(emailEditText)
        inputLayout.addView(passwordEditText)
        inputLayout.addView(registerButton)
        val frameLayout = FrameLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        frameLayout.addView(inputLayout)
        val backLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
        }
        frameLayout.addView(backButton, backLayoutParams)
        layout.addView(frameLayout)
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
                val users = JSONObject(usersJson)

                if (users.has(email)) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    users.put(email, password)
                    sharedPreferences.edit().putString(USERS_KEY, users.toString()).apply()
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    showHomePage(email)
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun transferKeys(oldEmail: String, newEmail: String) {
        val prefs = getSharedPreferences("messages", MODE_PRIVATE)
        val allMessages = prefs.all
        val editor = prefs.edit()

        for ((key, value) in allMessages) {
            Log.d("Mesaje","Mesaj: $key, $value")
            if (key.startsWith("chat_") && key.contains(oldEmail)) {
                val parts = key.removePrefix("chat_").split("_")
                if (parts.size != 2) {
                    val x= parts.size
                    Log.d("Mesaje","$x")
                    continue
                }

                var user1 = parts[0]
                var user2 = parts[1]
                user1 = if (user1 == oldEmail) newEmail else user1
                user2 = if (user2 == oldEmail) newEmail else user2
                Log.d("User","Useri: $user1, $user2")
                val newKey = "chat_${user1}_$user2"
                val oldMessages = value as? String ?: continue
                val updatedMessages = oldMessages.lines().joinToString("\n") { line ->
                    val msgParts = line.split("|")
                    if (msgParts.size == 3) {
                        Log.d("O ia pe aici","$msgParts[0], $msgParts[1]")
                        val sender = if (msgParts[0] == oldEmail) newEmail else msgParts[0]
                        "$sender|${msgParts[1]}|${msgParts[2]}"
                    } else {
                        line
                    }
                }
                editor.putString(newKey, updatedMessages)
                editor.remove(key)
            }
        }

        editor.apply()
    }

    private fun showEditProfileUI(email: String) {
        layout.removeAllViews()

        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        val users = JSONObject(usersJson)

        val profilePrefs = getSharedPreferences("profile_data", MODE_PRIVATE)
        val currentName = profilePrefs.getString("name_$email", "")

        val title = TextView(this).apply {
            text = "Edit Profile"
            textSize = 22f
            setPadding(16, 16, 16, 16)
        }

        val nameInput = EditText(this).apply {
            hint = "Display name"
            setText(currentName)
        }

        val emailInput = EditText(this).apply {
            hint = "New email (leave unchanged if not editing)"
            setText(email)
        }

        val passwordInput = EditText(this).apply {
            hint = "New password (leave blank if unchanged)"
        }

        val saveButton = Button(this).apply {
            text = "Save Changes"
        }

        val backButton = Button(this).apply {
            text = "Back"
        }

        saveButton.setOnClickListener {
            val newName = nameInput.text.toString().trim()
            val newEmail = emailInput.text.toString().trim()
            val newPassword = passwordInput.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Name and email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newEmail != email && users.has(newEmail)) {
                Toast.makeText(this, "This email is already in use", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordToStore = if (newPassword.isNotEmpty()) newPassword else users.getString(email)
            if (newEmail != email) {
                users.remove(email)
            }
            users.put(newEmail, passwordToStore)
            sharedPreferences.edit().putString(USERS_KEY, users.toString()).apply()
            profilePrefs.edit().remove("name_$email").apply()
            profilePrefs.edit().putString("name_$newEmail", newName).apply()
            transferKeys(email, newEmail)

            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            showHomePage(newEmail)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(title)
        layout.addView(nameInput)
        layout.addView(emailInput)
        layout.addView(passwordInput)
        layout.addView(saveButton)
        layout.addView(backButton)
    }
    private fun showHomePage(email: String) {
        layout.removeAllViews()

        val welcomeText = TextView(this).apply {
            text = "Welcome, $email!"
            textSize = 24f
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val logoutButton = Button(this).apply {
            text = "Logout"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val addNotesButton = Button(this).apply {
            text = "Add Notes for Myself"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val viewNotesButton = Button(this).apply {
            text = "View My Notes"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val discussButton = Button(this).apply {
            text = "Discuss"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val editProfileButton = Button(this).apply {
            text = "Edit Profile"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        editProfileButton.setOnClickListener {
            showEditProfileUI(email)
        }
        discussButton.setOnClickListener {
            showUserListForChat(email)  // Show a list of users for chatting
        }


        logoutButton.setOnClickListener {
            showInitialMenu()
        }

        addNotesButton.setOnClickListener {
            showAddNotesUI(email)
        }

        viewNotesButton.setOnClickListener {
            showViewNotesUI(email)
        }

        layout.addView(welcomeText)
        layout.addView(addNotesButton)
        layout.addView(viewNotesButton)
        layout.addView(logoutButton)
        layout.addView(discussButton)
        layout.addView(editProfileButton)
    }
    private fun showUserListForChat(email: String) {
        layout.removeAllViews()
        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        val users = JSONObject(usersJson)
        val originalUserList = mutableListOf<String>()
        val lista_useri_necititi = getUsersWithUnreadMessages(email)
        Log.d("Lista useri necititi", "User List: $lista_useri_necititi")
        val userList = mutableListOf<String>()
        for (key in users.keys()) {
            if (key != email) {
                originalUserList.add(key)
                if (lista_useri_necititi.contains(key)) {
                    userList.add("$key (nou)")
                } else {
                    userList.add(key)
                }
            }
        }

        Log.d("showUserListForChat", "User List: $userList")

        if (userList.isEmpty()) {
            Toast.makeText(this, "No users available for chat", Toast.LENGTH_SHORT).show()
        } else {
            val userListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
            val userListView = ListView(this).apply {
                adapter = userListAdapter
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            }
            userListView.setOnItemClickListener { _, _, position, _ ->
                val selectedUser = userList[position]
                showChatUI(email, selectedUser)  // Începe chat-ul cu utilizatorul selectat
            }
            layout.addView(userListView)
            val bottomLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val backButton = Button(this).apply {
                text = "Back"
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            backButton.setOnClickListener {
                showHomePage(email)
            }
            bottomLayout.addView(backButton)
            layout.addView(bottomLayout)
        }
    }
    private fun showChatUI(email: String, selectedUser: String) {
        layout.removeAllViews()
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val chatLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        val chatMessages = getChatMessages(email, selectedUser).split("\n")
        for (message in chatMessages) {
            val parts = message.split("|")
            if (parts.size < 3) continue

            val senderEmail = parts[0]
            val timestamp = parts[1]
            val content = parts[2]

            val isCurrentUser = senderEmail == email
            val messageLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                gravity = if (isCurrentUser) Gravity.END else Gravity.START
            }
            val bubbleContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Mesajul propriu-zis
            val messageTextView = TextView(this).apply {
                text = content
                textSize = 16f
                setPadding(24, 16, 24, 16)
                background = GradientDrawable().apply {
                    cornerRadius = 40f
                    setColor(if (isCurrentUser) Color.parseColor("#DCF8C6") else Color.WHITE)
                }
                setTextColor(Color.BLACK)
            }

            // Textul de sub mesaj: ora + sender (daca e altcineva)
            val timestampTextView = TextView(this).apply {
                text = if (!isCurrentUser) "$senderEmail · $timestamp" else timestamp
                textSize = 12f
                setTextColor(Color.GRAY)
                setPadding(16, 4, 16, 8)
                gravity = Gravity.END
            }
            bubbleContainer.addView(messageTextView)
            bubbleContainer.addView(timestampTextView)
            messageLayout.addView(bubbleContainer)
            chatLayout.addView(messageLayout)
        }

        scrollView.addView(chatLayout)
        val messageInputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
        }
        val messageEditText = EditText(this).apply {
            hint = "Enter your message"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) // Lățimea este flexibilă, iar înălțimea se ajustează automat
            setPadding(16, 16, 16, 16)
        }

        val sendButton = Button(this).apply {
            //text = "Send"
            layoutParams = LinearLayout.LayoutParams(200, 100) // Dimensiune fixă pentru buton (lățime și înălțime)
            setBackgroundResource(R.drawable.sendbutton) // Setează imaginea butonului
            setPadding(16, 16, 16, 16) // Setează padding pentru buton
        }
        messageInputLayout.addView(messageEditText)
        messageInputLayout.addView(sendButton)
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                saveChatMessage(email, selectedUser, message)
                messageEditText.text.clear()
                showChatUI(email, selectedUser)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showUserListForChat(email)
        }
        mainLayout.addView(scrollView)
        mainLayout.addView(messageInputLayout)
        mainLayout.addView(backButton)
        layout.addView(mainLayout)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun getAllMessages(): Map<String, String> {
        val prefs = sharedPreferences
        val allMessages = prefs.all
        val chatMessages = mutableMapOf<String, String>()
        for ((key, value) in allMessages) {
                val messages = value as? String ?: continue
                chatMessages[key] = messages
        }
        return chatMessages
    }
    private fun generateChatKey(user1: String, user2: String): String {
        val (emailA, emailB) = if (user1 < user2) Pair(user1, user2) else Pair(user2, user1)
        return "chat_${emailA}_${emailB}"
    }
    private fun saveChatMessage(sender: String, receiver: String, message: String) {
        val chatKey = generateChatKey(sender, receiver)
        val currentChat = sharedPreferences.getString(chatKey, "")

        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val fullMessage = "$sender|$timestamp|$message"

        val updatedChat = if (currentChat.isNullOrEmpty()) fullMessage else "$currentChat\n$fullMessage"

        sharedPreferences.edit().putString(chatKey, updatedChat).apply()
    }

    private fun getChatMessages(user1: String, user2: String): String {
        val chatKey = generateChatKey(user1, user2)
        return sharedPreferences.getString(chatKey, "") ?: "No messages yet."
    }
    fun getUserEmail(): String {
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        return prefs.getString("user_email", "default_email@example.com") ?: "default_email@example.com"
    }
    override fun onDestroy() {
        super.onDestroy()
        val userId = getUserEmail()
        saveLastLoginTime(userId)
    }
    fun saveLastLoginTime(userId: String) {
        val currentTimestamp = System.currentTimeMillis().toString()
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        prefs.edit().putString("last_login_time_$userId", currentTimestamp).apply()
    }
    fun getUsersWithUnreadMessages(userId: String): List<String> {
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        val usersWithUnreadMessages = mutableListOf<String>()

        val usersJson = prefs.getString("users", "{}")
        val users = JSONObject(usersJson)

        val lastLoginTime = prefs.getString("last_login_time_$userId", "0")?.toLong() ?: 0

        for (key in users.keys()) {
            if (key != userId) {
                val chatKey = "chat_${key}_$userId"
                val chatMessages = prefs.getString(chatKey, "") ?: ""

                val messages = chatMessages.split("\n")
                for (message in messages) {
                    val parts = message.split("|")
                    if (parts.size >= 3) {
                        val timestamp = parts[1].toLong()
                        if (timestamp > lastLoginTime) {
                            usersWithUnreadMessages.add(key)
                            break
                        }
                    }
                }
            }
        }

        return usersWithUnreadMessages
    }



    private fun showViewNotesUI(email: String) {
        layout.removeAllViews()

        val notesTextView = TextView(this).apply {
            text = "Your Notes:\n" + getSavedNotesForUser(email)
            textSize = 18f
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(notesTextView)
        layout.addView(backButton)
    }
    private fun getSavedNotesForUser(email: String): String {
        val savedNotes = sharedPreferences.getString(email, "")
        return savedNotes ?: "No notes available."
    }
    private fun showAddNotesUI(email: String) {
        layout.removeAllViews()

        val noteEditText = EditText(this).apply {
            hint = "Enter your note here"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val saveButton = Button(this).apply {
            text = "Save Note"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(noteEditText)
        layout.addView(saveButton)
        layout.addView(backButton)

        saveButton.setOnClickListener {
            val note = noteEditText.text.toString()

            if (note.isNotEmpty()) {
                val savedNotes = sharedPreferences.getString(email, "")
                val updatedNotes = if (savedNotes.isNullOrEmpty()) {
                    note
                } else {
                    "$savedNotes\n$note"
                }

                sharedPreferences.edit().putString(email, updatedNotes).apply()

                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                showHomePage(email)
            } else {
                Toast.makeText(this, "Please write something!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

