# ClipVault

Automatic clipboard organizer — copy ki hui har cheez khud-ba-khud sahi folder mein save ho jaati hai:
- **Numbers** — copied phone numbers
- **Links** — copied normal URLs
- **Text** — baqi sab kuch
- **Locked** — adult-related links, PIN se protected

Developer name (Sarfraz Qureshi) main screen par hai, tap karne par ek info popup khulta hai.

---

## Bina PC ke APK kaise banayen (Phone se GitHub Actions)

Aap ke paas PC nahi hai, isliye is project ko GitHub par upload karen aur GitHub ka server khud APK bana dega — poora kaam phone ke browser se ho jayega.

### Step 1 — GitHub account
Agar nahi hai to [github.com](https://github.com) par phone browser se free account banayen.

### Step 2 — Naya repository banayen
1. GitHub website kholen → "+" icon → "New repository"
2. Naam den (e.g. `ClipVault`) → Public ya Private (dono chalega) → "Create repository"

### Step 3 — Files upload karen
1. Is zip file ko apne phone mein extract karen (koi bhi file manager app se, ya "RAR"/"ZArchiver" app se)
2. GitHub repo ke andar "Add file" → "Upload files" par tap karen
3. Extract ki hui files/folders select karke upload kar den (poora `ClipVault` folder ka content, root mein)
4. Neeche "Commit changes" dabayen

### Step 4 — Build khud shuru ho jayega
1. Repo ke "Actions" tab par jayen
2. "Build APK" workflow chalta hua dikhega (2-4 minute lagte hain)
3. Green tick ka matlab build kamyab

### Step 5 — APK download karen
1. Usi completed workflow run par tap karen
2. Neeche "Artifacts" section mein "ClipVault-debug-apk" milega → download karen (zip milegi, usme APK hoga)

### Step 6 — Phone par install karen
1. Settings → Security → "Install unknown apps" → apna Browser/File Manager allow karen
2. APK file par tap karke install karen

### Step 7 — Zaroori permission ON karen (bohot important)
App khulte hi ek popup aayega jo Accessibility Settings kholne ko kahega:
1. "Settings kholen" dabayen
2. ClipVault dhoondh kar ON karen
3. Confirm karen

**Ye step na karen to copy ki hui cheezein automatically save nahi hongi** — ye Android ka apna security rule hai, koi bhi normal app is ke bina background clipboard read nahi kar sakti.

### Step 8 — "Locked" folder ka PIN
Pehli dafa "Locked" tab par tap karne par app aap se naya PIN set karwayega (4-6 digit). Us ke baad har dafa yehi PIN dalna hoga.

---

## Developer info edit karna
`app/src/main/res/values/strings.xml` file mein ye lines edit karen apne asal details ke saath:
```
dev_whatsapp, dev_email, dev_skills, dev_passion
```

## Adult-keyword list edit karna
`app/src/main/java/com/sarfrazqureshi/clipvault/util/ClipClassifier.kt` mein `adultKeywords` list hai — is mein words add/remove kar sakte hain.

---

## Nota (important limitation)
Koi bhi app, bilkul is tarah ke real clipboard-manager apps ki tarah (Play Store par maujood), sirf tab background mein clipboard read kar sakti hai jab Accessibility permission ON ho. Ye Google ka privacy rule hai, koi bug nahi.
