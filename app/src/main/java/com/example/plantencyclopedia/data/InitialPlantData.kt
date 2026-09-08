package com.example.plantencyclopedia.data

object InitialPlantData {
    val defaultPlants = listOf(
        Plant(
            id = 1,
            name = "الميرمية",
            english = "Sage",
            scientific = "Salvia officinalis",
            family = "الشفوية",
            usage = "علاجية",
            chemicals = listOf("حمض الروزمارينيك", "الثوجون", "سينيول"),
            note = "نبات عطري معمر، تشتهر أوراقه المخملية برائحتها النفاذة واستخداماتها التقليدية لتطهير الحلق وتهدئة اضطرابات الجهاز الهضمي.",
            image = "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85",
                "https://images.unsplash.com/photo-1515586000433-45406d8e6662?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "حوض البحر الأبيض المتوسط، يفضل الأراضي الكلسية المشمسة جيدة التصريف.",
            partsUsed = "الأوراق الغضة والمجففة والقمم الزهرية.",
            preparation = "منقوع الأوراق في ماء مغلي لمدة ٧-١٠ دقائق، أو غرغرة مطهرة.",
            precautions = "تجنب الإفراط في الجرعات العالية أثناء الحمل والإرضاع لاحتوائها على الثوجون.",
            growthForm = "شجيرة عشبية معمرة دائمة الخضرة يصل ارتفاعها إلى ٦٠ سم."
        ),
        Plant(
            id = 2,
            name = "الزعتر",
            english = "Thyme",
            scientific = "Thymus vulgaris",
            family = "الشفوية",
            usage = "غذائية",
            chemicals = listOf("الثيمول", "الكارفاكرول", "فلافونويدات"),
            note = "عشبة برية صغيرة ذات نكهة قوية ومميزة وخصائص مضادة للبكتيريا ومطهرة للمجاري التنفسية.",
            image = "https://images.unsplash.com/photo-1530968033775-2c92736b131e?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1530968033775-2c92736b131e?auto=format&fit=crop&w=800&q=85",
                "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "المناطق الصخرية المشمسة والمناخات المعتدلة والجافة.",
            partsUsed = "الأوراق الخضراء والمجففة والأفرع المزهرة.",
            preparation = "يغلى كشاي عشبي ساخن مع عسل النحل، أو يضاف طازجاً للأطعمة والزيوت.",
            precautions = "آمن عموماً؛ يفضل عدم استخدام الزيت العطري المركز داخلياً دون استشارة.",
            growthForm = "نبات عشبي قزمي دائم الخضرة مع سيقان متخشبة رمادية."
        ),
        Plant(
            id = 3,
            name = "الخزامى",
            english = "Lavender",
            scientific = "Lavandula angustifolia",
            family = "الشفوية",
            usage = "عطرية",
            chemicals = listOf("لينالول", "خلات الليناليل", "كافور"),
            note = "أزهار بنفسجية ساحرة ذات رائحة عطرية مهدئة تُستخدم على نطاق واسع في محاربة الأرق وتسكين الصداع.",
            image = "https://images.unsplash.com/photo-1499002238440-d264edd596ec?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1499002238440-d264edd596ec?auto=format&fit=crop&w=800&q=85",
                "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "الهضاب المشمسة والمناطق الجبلية الجافة جيدة التهوية.",
            partsUsed = "السنبلات الزهرية والزيوت الطيارة المستخلصة بالتقطير.",
            preparation = "استنشاق الزيت العطري، أكياس زهور مجففة تحت الوسادة، أو شاي خفيف.",
            precautions = "عدم ملامسة الزيت النقي للأعين، واختبار حساسية الجلد قبل الاستخدام الموضعي.",
            growthForm = "شجيرة معمرة متفرعة بأوراق رمادية مخضرة وسنابل زهرية قائمة."
        ),
        Plant(
            id = 4,
            name = "النعناع",
            english = "Peppermint",
            scientific = "Mentha × piperita",
            family = "الشفوية",
            usage = "غذائية",
            chemicals = listOf("المنثول", "المنثون", "سينامالديهيد"),
            note = "نبات عشبي منعش سريع الانتشار، يمنح إحساساً بالبرودة والانتعاش ويهدئ تشنجات المعدة وعسر الهضم.",
            image = "https://images.unsplash.com/photo-1530968033775-2c92736b131e?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1530968033775-2c92736b131e?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "الأراضي الرطبة الغنية وضفاف الجداول والمناطق المعتدلة المظللة جزئياً.",
            partsUsed = "الأوراق الغضة والمجففة والزيت الطيار.",
            preparation = "نقع الأوراق الطازجة في الماء الساخن أو تناولها كمنكه للمشروبات الباردة.",
            precautions = "قد يسبب زيادة ارتداد حمض المعدة لدى المصابين بفتق الحجاب الحاجز.",
            growthForm = "عشبة معمرة سريعة الامتداد بجذور زاحفة وسيقان مربعة قائمة."
        ),
        Plant(
            id = 5,
            name = "الورد الجوري",
            english = "Damask Rose",
            scientific = "Rosa × damascena",
            family = "الوردية",
            usage = "تجميلية",
            chemicals = listOf("سيترونيلول", "جيرانيول", "فارنيسول"),
            note = "زهرة شرقية أصيلة غنية بالمركبات المضادة للأكسدة، يستخلص منها ماء الورد وزيت الورد الملكي لنضارة البشرة.",
            image = "https://images.unsplash.com/photo-1495231916356-a86217efff12?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1495231916356-a86217efff12?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "الوديان الجبلية المعتدلة والتربة الخصبة الغنية بالمغذيات.",
            partsUsed = "بتلات الأزهار المتفتحة في الصباح الباكر.",
            preparation = "تقطير ماء الورد الطبيعي، زيوت تدليك، وكمادات مهدئة لمحيط العينين.",
            precautions = "آمن للغاية، يلزم التأكد من نقاء ماء الورد وخلوه من العطور الصناعية.",
            growthForm = "شجيرة شائكة متساقطة الأوراق بأزهار عطرية وردية كثيفة البتلات."
        ),
        Plant(
            id = 6,
            name = "البابونج",
            english = "Chamomile",
            scientific = "Matricaria chamomilla",
            family = "النجمية",
            usage = "علاجية",
            chemicals = listOf("كامازولين", "بيسابولول", "أبيجينين"),
            note = "أزهار رقيقة مهدئة للجهاز الهضمي والعصبي، تحارب الالتهابات وتساعد في علاج تقرحات الفم وتهيج الجلد.",
            image = "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "الحقول المفتوحة والمروج المشمسة والتربة الرملية الخفيفة.",
            partsUsed = "الرؤوس الزهرية المجففة فقط.",
            preparation = "شاي عشبي مغطى لمدة ٨ دقائق، أو كمادات دافئة للجلد والعيون.",
            precautions = "نادر الحدوث: حساسية لدى الأشخاص المتحسسين من نباتات الفصيلة النجمية.",
            growthForm = "نبات عشبي حولي ذو أوراق ريشية وأزهار شعاعية بيضاء بقرص أصفر مخروطي."
        ),
        Plant(
            id = 7,
            name = "إكليل الجبل",
            english = "Rosemary",
            scientific = "Salvia rosmarinus",
            family = "الشفوية",
            usage = "عطرية",
            chemicals = listOf("سينول", "كافور", "حمض الروزمارينيك"),
            note = "شجيرة عطرية خشبية، تحسن تدفق الدورة الدموية الدقيقة في فروة الرأس وتعزز الذاكرة والتركيز الذهني.",
            image = "https://images.unsplash.com/photo-1515586000433-45406d8e6662?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1515586000433-45406d8e6662?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "المناطق الساحلية الصخرية وحوض المتوسط، يتحمل الجفاف والرياح الشديدة.",
            partsUsed = "الأوراق الإبرية الضيقة والأطراف المزهرة العلوية.",
            preparation = "مغلي مقوي للشعر، أو زيت متبل للأطعمة، أو شاي مقوٍ للتركيز.",
            precautions = "يجب الحذر لدى مرضى ارتفاع ضغط الدم والصرع عند استعمال الجرعات الكبيرة.",
            growthForm = "شجيرة دائمة الخضرة كثيفة بأوراق جلدية إبرية وأزهار زرقاء باهتة."
        ),
        Plant(
            id = 8,
            name = "الصبار",
            english = "Aloe Vera",
            scientific = "Aloe barbadensis",
            family = "البروقية",
            usage = "تجميلية",
            chemicals = listOf("ألوين", "عديد السكاريد", "فيتامين هـ"),
            note = "نبات عصاري شهير بالهلام الشفاف المعجزة المرطب والمجدد لأنسجة الجلد والحروق السطحية وحروق الشمس.",
            image = "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "المناطق القاحلة وشبه القاحلة ذات درجات الحرارة المرتفعة والشمس الساطعة.",
            partsUsed = "اللب الجيلاتيني الشفاف من داخل الأوراق السميكة.",
            preparation = "استخراج الجل مباشرة ودهنه موضعياً على الجلد أو الشعر.",
            precautions = "تجنب الطبقة الصفراء الخارجية (الألوين) لاحتوائها على مواد مسهلة قوية مسببة للمغص.",
            growthForm = "نبات عصاري معمر دون ساق بأوراق لحمية خنجرية مسننة الحواف."
        ),
        Plant(
            id = 9,
            name = "الينبوت",
            english = "Prosopis Farcta",
            scientific = "Prosopis farcta",
            family = "البقولية",
            usage = "علاجية",
            chemicals = listOf("تانينات", "فلافونويدات", "ليكوسيانيدين"),
            note = "نبات شوكي بري تراثي عميق الجذور ينمو في بيئاتنا الجافة، استُعمل شعبياً لعلاج حصى المسالك وتسكين آلام المفاصل وتخفيض السكر.",
            image = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "السهول الصحراوية وحواف الأودية والتربة الرسوبية العميقة.",
            partsUsed = "القرون الثمرية المتضخمة، الجذور، والأوراق الجافة.",
            preparation = "مغلي الثمار المجففة أو مسحوق القرون المنقوعة.",
            precautions = "يؤخذ بحذر لدى المصابين بقصور الكلى المزمن ولأصحاب الضغط المنخفض.",
            growthForm = "شجيرة شائكة منخفضة ذات جذور ضاربة في عمق الأرض لامتصاص المياه الجوفية."
        ),
        Plant(
            id = 10,
            name = "اليانسون",
            english = "Anise",
            scientific = "Pimpinella anisum",
            family = "الخيمية",
            usage = "غذائية",
            chemicals = listOf("الأنيثول", "إستر كافيك", "تيربينويد"),
            note = "بذور عطرية حلوة المذاق طاردة للغازات ومهدئة لسعال الصدر وتشنجات القولون والأمعاء للأطفال والكبار.",
            image = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=800&q=85",
            images = listOf(
                "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?auto=format&fit=crop&w=800&q=85"
            ),
            habitat = "المناطق الشرق أوسطية الدافئة والتربة الخفيفة ذات الصرف الممتاز.",
            partsUsed = "الثمار الجافة (البذور).",
            preparation = "غلي ملعقة صغيرة من البذور بلطف في ماء نقي وتغطيتها ٥ دقائق.",
            precautions = "آمن في الاستخدام الغذائي الطبيعي؛ تجنب الاستهلاك المفرط لمركبات الزيت النقي.",
            growthForm = "نبات عشبي حولي يحمل مظلات زهرية خيمية بيضاء وثماراً رمادية مخططة."
        )
    )
}

