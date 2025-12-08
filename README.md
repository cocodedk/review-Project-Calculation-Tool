# 📂 Project Management System  
Et webbaseret projektstyringssystem udviklet i Java, Spring Boot & Thymeleaf.

## 🚀 Formål  
Systemet gør det muligt for projektledere og teammedlemmer at samarbejde om projekter, delprojekter og opgaver (tasks).  
Brugere kan logge ind som enten **Project Manager** eller **Team Member**, og deres rettigheder varierer derefter.

---

## 🧑‍💻 Roller i systemet

### 👨‍💼 Project Manager
- Kan se alle projekter og opgaver  
- Kan oprette projekter, subprojekter og tasks  
- Kan ikke ændre task-status (kun læse)  
- Har overblik over fremdrift på projekter  

### 👷 Team Member
- Kan se de projekter, de er tildelt  
- Kan **ændre status på opgaver**, f.eks.:  
  - Ikke startet → I gang → Afsluttet  
- Har adgang til egne tasks og noter  
- Kan kommentere og opdatere fremdrift  

---

## 🏗 Systemfunktioner

### ✔ Projektstyring
- Opret og administrer projekter  
- Underprojekter (SubProjects)  
- Opgaver (Tasks)

### ✔ Task-funktioner
- Titel, beskrivelse, deadline  
- Status (enum):  
  - `NOT_STARTED`  
  - `IN_PROGRESS`  
  - `COMPLETED`  
- Mulighed for subtasks  
- Automatisk opdatering af status

### ✔ Login og roller
- Rollebaseret adgang (Project Manager / Team Member)  
- Forskellige handlinger afhængig af login

### ✔ Live opdatering
Når teammedlem ændrer en task-status, bliver ændringen gemt i databasen og kan straks ses af project manager.

---

## 🛠 Tech Stack

| Teknologi | Brug |
|----------|------|
| **Java 17+** | Backend |
| **Spring Boot** | Web, MVC, Security |
| **Thymeleaf** | HTML views |
| **Spring Data JPA** | Database lag |
| **MySQL / H2** | Database |
| **Lombok** | Reducerer boilerplate kode |
| **Git + GitHub** | Versionsstyring |

---

## 📦 Installation & Kørsel

### 1. Klon projektet
``bash
git clone [ https://github.com/DIT_REPO_NAVN](https://github.com/KimL92/Project-Calculation-Tool.git)
cd projektmappen

---

## 👥 Contributors

Tak til alle som har bidraget til projektet:

<a href="https://github.com/KimL92/Project-Calculation-Tool/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=KimL92/Project-Calculation-Tool" />
</a>

### Individuelle GitHub-profiler
- [@aden0020](https://github.com/Aden0020)
- [@aljamour](https://github.com/aljamour)
- [@Junes2003](https://github.com/Junes2003)
- [@moha4733](https://github.com/moha4733)
