// Данные приложения
let galleryItems = [];
let mediaFiles = new Map(); // Хранение файлов с уникальными именами
let currentFiles = [];

// Текстовые данные из AppTexts.java
let appTexts = {
    loveMessages: [
        "🤍 коткночек мой любимый, ты у меня самая самая!!",
        "💕 мы со всем всем справимся, я клянусь!!",
        "🌟 ты моя огромная умничка и я горжусь тобой прям!!",
        "🫂 кис, я тебя не оставлю какая ситуация бы не случилась",
        "💪 при любой возможности тебе помочь я это сделаю!!"
    ],
    missions: [
        ["Утренняя нежность", "Пошли мне голосовое сообщение с пожеланием доброго утра 🌅"],
        ["Селфи с улыбкой", "Сделай селфи с самой красивой улыбкой и пришли мне 😊"],
        ["Комплимент дня", "Напиши мне три вещи, которые тебе во мне нравятся 💕"]
    ],
    jokes: [
        "коткночек, знаешь почему я не могу без тебя? потому что без тебя жизнь становится слишком серьезной!! 😄",
        "- доктор, я влюблен!\n- это не болезнь.\n- а почему тогда так кружится голова от тебя? 💫"
    ],
    facts: [
        "🦋 факт: бабочки видят мир в ультрафиолете. но даже они не видят ничего прекраснее твоей улыбки!",
        "🌟 знаешь ли ты что в галактике 100 миллиардов звезд? но ты затмеваешь их все!"
    ],
    journeyContent: [
        ["День 1: Знакомство", "Сегодня особенный день - мы только знакомимся! Расскажи мне о себе что-то интересное 💕"],
        ["День 2: Первые сообщения", "Наши первые сообщения... Помнишь что ты тогда подумала обо мне? 😊"]
    ]
};

// Инициализация приложения
document.addEventListener('DOMContentLoaded', function() {
    loadTextCategory();
    updateStatistics();
    setupDragAndDrop();
    
    // Загружаем сохраненные данные
    loadSavedData();
});

// Настройка drag & drop
function setupDragAndDrop() {
    const dropZone = document.querySelector('.file-drop-zone');
    
    dropZone.addEventListener('dragover', function(e) {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });
    
    dropZone.addEventListener('dragleave', function(e) {
        e.preventDefault();
        dropZone.classList.remove('dragover');
    });
    
    dropZone.addEventListener('drop', function(e) {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
    });
}

// Переключение между секциями
function showSection(sectionId) {
    // Скрыть все секции
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Убрать активный класс со всех табов
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Показать выбранную секцию
    document.getElementById(sectionId).classList.add('active');
    
    // Активировать соответствующий таб
    event.target.classList.add('active');
    
    // Обновить статистику для экспорта
    if (sectionId === 'export') {
        updateExportStatistics();
    }
}

// Обработка файлов
function handleFiles(files) {
    currentFiles = Array.from(files);
    displayFilePreview();
}

// Отображение превью файлов
function displayFilePreview() {
    const preview = document.getElementById('file-preview');
    preview.innerHTML = '';
    
    if (currentFiles.length === 0) {
        return;
    }
    
    preview.innerHTML = '<h4>📁 Выбранные файлы:</h4>';
    
    currentFiles.forEach((file, index) => {
        const fileDiv = document.createElement('div');
        fileDiv.style.display = 'inline-block';
        fileDiv.style.margin = '10px';
        fileDiv.style.textAlign = 'center';
        
        if (file.type.startsWith('image/')) {
            const img = document.createElement('img');
            img.src = URL.createObjectURL(file);
            img.style.maxWidth = '150px';
            img.style.maxHeight = '150px';
            img.style.borderRadius = '10px';
            fileDiv.appendChild(img);
        } else if (file.type.startsWith('video/')) {
            const video = document.createElement('video');
            video.src = URL.createObjectURL(file);
            video.controls = true;
            video.style.maxWidth = '150px';
            video.style.maxHeight = '150px';
            video.style.borderRadius = '10px';
            fileDiv.appendChild(video);
        }
        
        const fileName = document.createElement('p');
        fileName.textContent = file.name;
        fileName.style.marginTop = '5px';
        fileName.style.fontSize = '0.8rem';
        fileName.style.color = '#666';
        fileDiv.appendChild(fileName);
        
        preview.appendChild(fileDiv);
    });
}

// Добавление элемента в галерею
function addGalleryItem() {
    const title = document.getElementById('item-title').value.trim();
    const description = document.getElementById('item-description').value.trim();
    
    if (!title || !description) {
        alert('Пожалуйста, заполните заголовок и описание');
        return;
    }
    
    if (currentFiles.length === 0) {
        alert('Пожалуйста, выберите файлы для добавления');
        return;
    }
    
    currentFiles.forEach(file => {
        const fileExtension = file.name.split('.').pop().toLowerCase();
        const type = file.type.startsWith('image/') ? 'photo' : 'video';
        
        // Автоматическое переименование под нужный формат для Android приложения
        let androidFileName;
        if (type === 'photo') {
            const photoCount = galleryItems.filter(item => item.type === 'photo').length + 1;
            androidFileName = `photo${photoCount}.${fileExtension}`;
        } else {
            const videoCount = galleryItems.filter(item => item.type === 'video').length + 1;
            androidFileName = `video${videoCount}`; // Без расширения для видео в Android
        }
        
        const galleryItem = {
            type: type,
            fileName: androidFileName,
            title: title,
            description: description,
            originalFile: file,
            originalFileName: file.name
        };
        
        galleryItems.push(galleryItem);
        mediaFiles.set(androidFileName, file);
    });
    
    // Очистка формы
    document.getElementById('item-title').value = '';
    document.getElementById('item-description').value = '';
    clearCurrentFiles();
    
    // Обновление отображения
    updateGalleryDisplay();
    updateStatistics();
    saveData();
    
    alert(`Добавлено ${currentFiles.length} элемент(ов) в галерею!`);
}

// Очистка выбранных файлов
function clearCurrentFiles() {
    currentFiles = [];
    document.getElementById('file-input').value = '';
    document.getElementById('file-preview').innerHTML = '';
}

// Обновление отображения галереи
function updateGalleryDisplay() {
    const container = document.getElementById('gallery-items');
    container.innerHTML = '';
    
    if (galleryItems.length === 0) {
        container.innerHTML = '<p style="color: #666; text-align: center; padding: 20px;">Пока нет элементов в галерее</p>';
        return;
    }
    
    galleryItems.forEach((item, index) => {
        const itemDiv = document.createElement('div');
        itemDiv.className = 'gallery-item';
        
        itemDiv.innerHTML = `
            <div class="type-badge ${item.type === 'photo' ? 'type-photo' : 'type-video'}">
                ${item.type === 'photo' ? '📷 Фото' : '🎬 Видео'}
            </div>
            <h4>${item.title}</h4>
            <p><strong>Исходный файл:</strong> ${item.originalFileName || item.fileName}</p>
            <p><strong>Имя в приложении:</strong> <code>${item.fileName}</code></p>
            <p><strong>Описание:</strong> ${item.description}</p>
            <button class="btn btn-danger" onclick="removeGalleryItem(${index})">🗑️ Удалить</button>
        `;
        
        container.appendChild(itemDiv);
    });
}

// Удаление элемента галереи
function removeGalleryItem(index) {
    if (confirm('Вы уверены, что хотите удалить этот элемент?')) {
        const item = galleryItems[index];
        mediaFiles.delete(item.fileName);
        galleryItems.splice(index, 1);
        
        // Перенумеровать оставшиеся файлы
        renumberGalleryItems();
        
        updateGalleryDisplay();
        updateStatistics();
        saveData();
    }
}

// Перенумерация файлов после удаления
function renumberGalleryItems() {
    const newMediaFiles = new Map();
    let photoCounter = 1;
    let videoCounter = 1;
    
    galleryItems.forEach(item => {
        const oldFileName = item.fileName;
        const oldFile = mediaFiles.get(oldFileName);
        
        if (item.type === 'photo') {
            const fileExtension = item.originalFileName ? 
                item.originalFileName.split('.').pop().toLowerCase() : 
                oldFileName.split('.').pop().toLowerCase();
            item.fileName = `photo${photoCounter}.${fileExtension}`;
            photoCounter++;
        } else {
            item.fileName = `video${videoCounter}`;
            videoCounter++;
        }
        
        if (oldFile) {
            newMediaFiles.set(item.fileName, oldFile);
        }
    });
    
    mediaFiles.clear();
    newMediaFiles.forEach((value, key) => {
        mediaFiles.set(key, value);
    });
}

// Обновление статистики
function updateStatistics() {
    const photoCount = galleryItems.filter(item => item.type === 'photo').length;
    const videoCount = galleryItems.filter(item => item.type === 'video').length;
    const totalCount = galleryItems.length;
    
    document.getElementById('photo-count').textContent = photoCount;
    document.getElementById('video-count').textContent = videoCount;
    document.getElementById('total-count').textContent = totalCount;
}

// Обновление статистики экспорта
function updateExportStatistics() {
    document.getElementById('export-gallery-count').textContent = galleryItems.length;
    document.getElementById('export-love-count').textContent = appTexts.loveMessages.length;
    document.getElementById('export-mission-count').textContent = appTexts.missions.length;
    document.getElementById('export-files-count').textContent = mediaFiles.size;
}

// === РАБОТА С ТЕКСТАМИ ===

// Загрузка категории текстов
function loadTextCategory() {
    const category = document.getElementById('text-category').value;
    const titleElement = document.getElementById('text-list-title');
    const missionGroup = document.getElementById('mission-description-group');
    
    // Показать/скрыть поле описания для миссий
    if (category === 'missions' || category === 'journey') {
        missionGroup.style.display = 'block';
    } else {
        missionGroup.style.display = 'none';
    }
    
    // Обновить заголовок
    const titles = {
        'love-messages': '💕 Сообщения любви:',
        'missions': '🎯 Миссии:',
        'jokes': '😄 Анекдоты:',
        'facts': '🧠 Факты:',
        'journey': '🗺️ Содержимое ходилки:'
    };
    titleElement.textContent = titles[category];
    
    displayTexts(category);
}

// Отображение текстов
function displayTexts(category) {
    const container = document.getElementById('text-items');
    container.innerHTML = '';
    
    let texts = [];
    switch(category) {
        case 'love-messages':
            texts = appTexts.loveMessages;
            break;
        case 'missions':
            texts = appTexts.missions;
            break;
        case 'jokes':
            texts = appTexts.jokes;
            break;
        case 'facts':
            texts = appTexts.facts;
            break;
        case 'journey':
            texts = appTexts.journeyContent;
            break;
    }
    
    if (texts.length === 0) {
        container.innerHTML = '<p style="color: #666; text-align: center; padding: 20px;">Нет текстов в этой категории</p>';
        return;
    }
    
    texts.forEach((text, index) => {
        const textDiv = document.createElement('div');
        textDiv.className = 'text-item';
        
        if (Array.isArray(text)) {
            // Для миссий и ходилки (массив из заголовка и описания)
            textDiv.innerHTML = `
                <h5>${text[0]}</h5>
                <p>${text[1]}</p>
                <button class="btn btn-danger" onclick="removeText('${category}', ${index})" style="margin-top: 10px;">🗑️ Удалить</button>
            `;
        } else {
            // Для обычных текстов
            textDiv.innerHTML = `
                <p>${text}</p>
                <button class="btn btn-danger" onclick="removeText('${category}', ${index})" style="margin-top: 10px;">🗑️ Удалить</button>
            `;
        }
        
        container.appendChild(textDiv);
    });
}

// Добавление текста
function addText() {
    const category = document.getElementById('text-category').value;
    const newText = document.getElementById('new-text').value.trim();
    const missionDescription = document.getElementById('mission-description').value.trim();
    
    if (!newText) {
        alert('Пожалуйста, введите текст');
        return;
    }
    
    if ((category === 'missions' || category === 'journey') && !missionDescription) {
        alert('Пожалуйста, введите описание');
        return;
    }
    
    let textToAdd;
    if (category === 'missions' || category === 'journey') {
        textToAdd = [newText, missionDescription];
    } else {
        textToAdd = newText;
    }
    
    switch(category) {
        case 'love-messages':
            appTexts.loveMessages.push(textToAdd);
            break;
        case 'missions':
            appTexts.missions.push(textToAdd);
            break;
        case 'jokes':
            appTexts.jokes.push(textToAdd);
            break;
        case 'facts':
            appTexts.facts.push(textToAdd);
            break;
        case 'journey':
            appTexts.journeyContent.push(textToAdd);
            break;
    }
    
    // Очистка формы
    document.getElementById('new-text').value = '';
    document.getElementById('mission-description').value = '';
    
    displayTexts(category);
    saveData();
    alert('Текст добавлен!');
}

// Удаление текста
function removeText(category, index) {
    if (confirm('Вы уверены, что хотите удалить этот текст?')) {
        switch(category) {
            case 'love-messages':
                appTexts.loveMessages.splice(index, 1);
                break;
            case 'missions':
                appTexts.missions.splice(index, 1);
                break;
            case 'jokes':
                appTexts.jokes.splice(index, 1);
                break;
            case 'facts':
                appTexts.facts.splice(index, 1);
                break;
            case 'journey':
                appTexts.journeyContent.splice(index, 1);
                break;
        }
        
        displayTexts(category);
        saveData();
    }
}

// Очистка всех текстов в категории
function clearAllTexts() {
    const category = document.getElementById('text-category').value;
    
    if (confirm('Вы уверены, что хотите удалить ВСЕ тексты в этой категории?')) {
        switch(category) {
            case 'love-messages':
                appTexts.loveMessages = [];
                break;
            case 'missions':
                appTexts.missions = [];
                break;
            case 'jokes':
                appTexts.jokes = [];
                break;
            case 'facts':
                appTexts.facts = [];
                break;
            case 'journey':
                appTexts.journeyContent = [];
                break;
        }
        
        displayTexts(category);
        saveData();
        alert('Все тексты удалены!');
    }
}

// === ЭКСПОРТ ДАННЫХ ===

// Экспорт GalleryConfig.java
function exportGalleryConfig() {
    let javaCode = `package com.napominalochka.app.config;

public class GalleryConfig {
    
    // ========== МЕДИА ЭЛЕМЕНТЫ ГАЛЕРЕИ ==========
    // Формат: {тип, имя_файла, заголовок, описание}
    // Типы: "photo" или "video"
    
    public static final String[][] GALLERY_ITEMS = {
`;

    galleryItems.forEach((item, index) => {
        const comma = index < galleryItems.length - 1 ? ',' : '';
        const escapedTitle = item.title.replace(/"/g, '\\"');
        const escapedDescription = item.description.replace(/"/g, '\\"');
        
        javaCode += `        {"${item.type}", "${item.fileName}", "${escapedTitle}", "${escapedDescription}"}${comma}\n`;
    });

    javaCode += `    };
    
    // Проверка, является ли элемент видео
    public static boolean isVideo(String[] item) {
        return "video".equals(item[0]);
    }
    
    // Получить имя файла
    public static String getFileName(String[] item) {
        return item[1];
    }
    
    // Получить заголовок
    public static String getTitle(String[] item) {
        return item[2];
    }
    
    // Получить описание
    public static String getDescription(String[] item) {
        return item[3];
    }
}`;

    downloadFile('GalleryConfig.java', javaCode);
}

// Экспорт AppTexts.java
function exportAppTexts() {
    let javaCode = `package com.napominalochka.app.config;

public class AppTexts {
    
    // ========== СЕКРЕТНЫЙ КОД ==========
    public static final String SECRET_CODE = "ЛЮБОВЬ";
    
    // ========== СООБЩЕНИЯ ЛЮБВИ (БАТАРЕЙКА) ==========
    public static final String[] LOVE_MESSAGES = {
`;

    appTexts.loveMessages.forEach((msg, index) => {
        const comma = index < appTexts.loveMessages.length - 1 ? ',' : '';
        const escapedMsg = msg.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedMsg}"${comma}\n`;
    });

    javaCode += `    };
    
    // ========== МИССИИ ==========
    public static final String[][] MISSIONS = {
`;

    appTexts.missions.forEach((mission, index) => {
        const comma = index < appTexts.missions.length - 1 ? ',' : '';
        const escapedTitle = mission[0].replace(/"/g, '\\"');
        const escapedDesc = mission[1].replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        {"${escapedTitle}", "${escapedDesc}"}${comma}\n`;
    });

    javaCode += `    };
    
    // ========== СОДЕРЖИМОЕ ИГРЫ-ХОДИЛКИ ==========
    public static final String[][] JOURNEY_CONTENT = {
`;

    appTexts.journeyContent.forEach((content, index) => {
        const comma = index < appTexts.journeyContent.length - 1 ? ',' : '';
        const escapedTitle = content[0].replace(/"/g, '\\"');
        const escapedDesc = content[1].replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        {"${escapedTitle}", "${escapedDesc}"}${comma}\n`;
    });

    javaCode += `    };
    
    // ========== АНЕКДОТЫ И ФАКТЫ (ГЕНЕРАТОР РАДОСТИ) ==========
    public static final String[] JOKES = {
`;

    appTexts.jokes.forEach((joke, index) => {
        const comma = index < appTexts.jokes.length - 1 ? ',' : '';
        const escapedJoke = joke.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedJoke}"${comma}\n`;
    });

    javaCode += `    };
    
    public static final String[] FACTS = {
`;

    appTexts.facts.forEach((fact, index) => {
        const comma = index < appTexts.facts.length - 1 ? ',' : '';
        const escapedFact = fact.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedFact}"${comma}\n`;
    });

    javaCode += `    };
    
    // ========== СЕКРЕТНОЕ СООБЩЕНИЕ ==========
    public static final String SECRET_MESSAGE = 
        "💖 ОСОБОЕ ПОСЛАНИЕ ДЛЯ ТЕБЯ 💖\\n\\n" +
        "поздравляю! ты нашла секретный ключ! 🗝️\\n\\n" +
        "этот сюрприз был создан специально для тебя, потому что:\\n\\n" +
        "🌟 ты освещаешь каждый мой день\\n" +
        "💫 с тобой я чувствую себя самым счастливым\\n" +
        "🦋 ты превращаешь обычные моменты в волшебство\\n" +
        "🌈 твоя улыбка - мой самый любимый цвет\\n" +
        "⭐ ты - звезда по которой я выбираю путь\\n\\n" +
        "это приложение - маленькая частичка моей души которую я дарю тебе. " +
        "каждая строчка кода написана с мыслями о тебе. 💻💕\\n\\n" +
        "спасибо за то что ты есть в моей жизни! 🙏\\n\\n" +
        "с бесконечной любовью,\\nтвой любящий разработчик 👨‍💻❤️";
}`;

    downloadFile('AppTexts.java', javaCode);
}

// Экспорт медиа файлов как ZIP
async function exportMediaFiles() {
    if (mediaFiles.size === 0) {
        alert('Нет медиа файлов для экспорта');
        return;
    }

    try {
        // Используем JSZip для создания архива
        const JSZip = await loadJSZip();
        const zip = new JSZip();
        
        // Создаем папки
        const photosFolder = zip.folder("photos");
        const videosFolder = zip.folder("videos");
        
        // Добавляем файлы в соответствующие папки с правильными именами
        for (const [androidFileName, file] of mediaFiles.entries()) {
            const isPhoto = file.type.startsWith('image/');
            const folder = isPhoto ? photosFolder : videosFolder;
            
            // Для фото сохраняем с расширением, для видео - без расширения (как в Android)
            let finalFileName = androidFileName;
            if (!isPhoto && !androidFileName.includes('.')) {
                // Для видео добавляем исходное расширение при сохранении в архив
                const originalExtension = file.name.split('.').pop().toLowerCase();
                finalFileName = `${androidFileName}.${originalExtension}`;
            }
            
            folder.file(finalFileName, file);
        }
        
        // Создаем архив
        const content = await zip.generateAsync({type: "blob"});
        downloadFile('media_files.zip', content);
        
    } catch (error) {
        console.error('Ошибка создания архива:', error);
        alert('Ошибка при создании архива. Попробуйте скачать файлы по отдельности.');
    }
}

// Экспорт всего одним архивом
async function exportAll() {
    try {
        const JSZip = await loadJSZip();
        const zip = new JSZip();
        
        // Добавляем Java файлы
        zip.file("GalleryConfig.java", generateGalleryConfigContent());
        zip.file("AppTexts.java", generateAppTextsContent());
        
        // Добавляем медиа файлы
        if (mediaFiles.size > 0) {
            const photosFolder = zip.folder("media/photos");
            const videosFolder = zip.folder("media/videos");
            
            for (const [androidFileName, file] of mediaFiles.entries()) {
                const isPhoto = file.type.startsWith('image/');
                const folder = isPhoto ? photosFolder : videosFolder;
                
                // Для фото сохраняем с расширением, для видео - без расширения (как в Android)
                let finalFileName = androidFileName;
                if (!isPhoto && !androidFileName.includes('.')) {
                    // Для видео добавляем исходное расширение при сохранении в архив
                    const originalExtension = file.name.split('.').pop().toLowerCase();
                    finalFileName = `${androidFileName}.${originalExtension}`;
                }
                
                folder.file(finalFileName, file);
            }
        }
        
        // Добавляем README
        const readme = `# Экспорт данных "Напоминалочка"

Этот архив содержит:

1. **GalleryConfig.java** - конфигурация галереи (${galleryItems.length} элементов)
2. **AppTexts.java** - все тексты приложения
3. **media/photos/** - фотографии (${galleryItems.filter(i => i.type === 'photo').length} файлов)
4. **media/videos/** - видео (${galleryItems.filter(i => i.type === 'video').length} файлов)

## Инструкция по установке:

1. Скопируйте Java файлы в папку app/src/main/java/com/napominalochka/app/config/
2. Скопируйте медиа файлы в соответствующие папки ресурсов Android проекта
3. Пересоберите приложение

Создано с помощью Менеджера галереи "Напоминалочка"
Дата экспорта: ${new Date().toLocaleString('ru-RU')}`;

        zip.file("README.md", readme);
        
        // Создаем архив
        const content = await zip.generateAsync({type: "blob"});
        downloadFile('napominalochka_export.zip', content);
        
        alert('Полный экспорт создан успешно!');
        
    } catch (error) {
        console.error('Ошибка создания полного архива:', error);
        alert('Ошибка при создании архива. Попробуйте экспортировать файлы по отдельности.');
    }
}

// Вспомогательные функции для генерации контента
function generateGalleryConfigContent() {
    // ... (код из exportGalleryConfig, но возвращаем строку вместо скачивания)
    let javaCode = `package com.napominalochka.app.config;

public class GalleryConfig {
    
    // ========== МЕДИА ЭЛЕМЕНТЫ ГАЛЕРЕИ ==========
    // Формат: {тип, имя_файла, заголовок, описание}
    // Типы: "photo" или "video"
    
    public static final String[][] GALLERY_ITEMS = {
`;

    galleryItems.forEach((item, index) => {
        const comma = index < galleryItems.length - 1 ? ',' : '';
        const escapedTitle = item.title.replace(/"/g, '\\"');
        const escapedDescription = item.description.replace(/"/g, '\\"');
        
        javaCode += `        {"${item.type}", "${item.fileName}", "${escapedTitle}", "${escapedDescription}"}${comma}\n`;
    });

    javaCode += `    };
    
    // Проверка, является ли элемент видео
    public static boolean isVideo(String[] item) {
        return "video".equals(item[0]);
    }
    
    // Получить имя файла
    public static String getFileName(String[] item) {
        return item[1];
    }
    
    // Получить заголовок
    public static String getTitle(String[] item) {
        return item[2];
    }
    
    // Получить описание
    public static String getDescription(String[] item) {
        return item[3];
    }
}`;

    return javaCode;
}

function generateAppTextsContent() {
    let javaCode = `package com.napominalochka.app.config;

public class AppTexts {
    
    // ========== СЕКРЕТНЫЙ КОД ==========
    public static final String SECRET_CODE = "ЛЮБОВЬ";
    
    // ========== СООБЩЕНИЯ ЛЮБВИ (БАТАРЕЙКА) ==========
    public static final String[] LOVE_MESSAGES = {
`;

    appTexts.loveMessages.forEach((msg, index) => {
        const comma = index < appTexts.loveMessages.length - 1 ? ',' : '';
        const escapedMsg = msg.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedMsg}"${comma}\n`;
    });

    javaCode += `    };
    
    // ========== МИССИИ ==========
    public static final String[][] MISSIONS = {
`;

    appTexts.missions.forEach((mission, index) => {
        const comma = index < appTexts.missions.length - 1 ? ',' : '';
        const escapedTitle = mission[0].replace(/"/g, '\\"');
        const escapedDesc = mission[1].replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        {"${escapedTitle}", "${escapedDesc}"}${comma}\n`;
    });

    javaCode += `    };
    
    // ========== СОДЕРЖИМОЕ ИГРЫ-ХОДИЛКИ ==========
    public static final String[][] JOURNEY_CONTENT = {
`;

    appTexts.journeyContent.forEach((content, index) => {
        const comma = index < appTexts.journeyContent.length - 1 ? ',' : '';
        const escapedTitle = content[0].replace(/"/g, '\\"');
        const escapedDesc = content[1].replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        {"${escapedTitle}", "${escapedDesc}"}${comma}\n`;
    });

    javaCode += `    };
    
    // ========== АНЕКДОТЫ И ФАКТЫ (ГЕНЕРАТОР РАДОСТИ) ==========
    public static final String[] JOKES = {
`;

    appTexts.jokes.forEach((joke, index) => {
        const comma = index < appTexts.jokes.length - 1 ? ',' : '';
        const escapedJoke = joke.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedJoke}"${comma}\n`;
    });

    javaCode += `    };
    
    public static final String[] FACTS = {
`;

    appTexts.facts.forEach((fact, index) => {
        const comma = index < appTexts.facts.length - 1 ? ',' : '';
        const escapedFact = fact.replace(/"/g, '\\"').replace(/\n/g, '\\n');
        javaCode += `        "${escapedFact}"${comma}\n`;
    });

    javaCode += `    };
    
    // ========== СЕКРЕТНОЕ СООБЩЕНИЕ ==========
    public static final String SECRET_MESSAGE = 
        "💖 ОСОБОЕ ПОСЛАНИЕ ДЛЯ ТЕБЯ 💖\\n\\n" +
        "поздравляю! ты нашла секретный ключ! 🗝️\\n\\n" +
        "этот сюрприз был создан специально для тебя, потому что:\\n\\n" +
        "🌟 ты освещаешь каждый мой день\\n" +
        "💫 с тобой я чувствую себя самым счастливым\\n" +
        "🦋 ты превращаешь обычные моменты в волшебство\\n" +
        "🌈 твоя улыбка - мой самый любимый цвет\\n" +
        "⭐ ты - звезда по которой я выбираю путь\\n\\n" +
        "это приложение - маленькая частичка моей души которую я дарю тебе. " +
        "каждая строчка кода написана с мыслями о тебе. 💻💕\\n\\n" +
        "спасибо за то что ты есть в моей жизни! 🙏\\n\\n" +
        "с бесконечной любовью,\\nтвой любящий разработчик 👨‍💻❤️";
}`;

    return javaCode;
}

// Загрузка JSZip библиотеки
async function loadJSZip() {
    if (window.JSZip) {
        return window.JSZip;
    }
    
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js';
        script.onload = () => resolve(window.JSZip);
        script.onerror = reject;
        document.head.appendChild(script);
    });
}

// Скачивание файла
function downloadFile(filename, content) {
    const blob = content instanceof Blob ? content : new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

// === СОХРАНЕНИЕ И ЗАГРУЗКА ДАННЫХ ===

function saveData() {
    const data = {
        galleryItems: galleryItems,
        appTexts: appTexts,
        timestamp: Date.now()
    };
    
    localStorage.setItem('napominalochka_data', JSON.stringify(data));
}

function loadSavedData() {
    const saved = localStorage.getItem('napominalochka_data');
    if (saved) {
        try {
            const data = JSON.parse(saved);
            if (data.galleryItems) {
                galleryItems = data.galleryItems;
                updateGalleryDisplay();
                updateStatistics();
            }
            if (data.appTexts) {
                appTexts = data.appTexts;
            }
        } catch (error) {
            console.error('Ошибка загрузки сохраненных данных:', error);
        }
    }
}