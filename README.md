# 🎉 EvenMoreFun - Sistema Interativo de Eventos

Projeto desenvolvido para a disciplina de **Estrutura de Dados** da **FATEC – Prof. Waldomiro May**.

O **EvenMoreFun** é uma aplicação desktop desenvolvida em **Java** com interface gráfica em **JavaFX**, voltada para a criação, gerenciamento e participação em eventos. A proposta é oferecer uma experiência rica e interativa tanto para organizadores quanto para participantes, integrando funcionalidades como **chat em tempo real**, **controle de presença**, **envio de e-mails** e **autenticação com OTP (código de uso único)**.

---

## 📚 Objetivo Acadêmico

Aplicar conceitos fundamentais de estruturas de dados — como listas, árvores, vetores e mapas — **sem o uso de banco de dados**, reforçando a lógica, boas práticas de desenvolvimento e princípios de orientação a objetos.

---

## ⚙️ Tecnologias Utilizadas

- **Java** → Linguagem principal da aplicação  
- **JavaFX** → Criação da interface gráfica  
- **SceneBuilder** → Design visual das telas  
- **Jasypt** → Criptografia de senhas  
- **JavaMail** → Envio de e-mails com anexos e OTP  
- **Collections Java (ArrayList, Set, Map)** → Estruturas de dados  
- **FXML + CSS** → Customização da interface  
- **IDE Eclipse** → Ambiente de desenvolvimento  

---

## 🧩 Funcionalidades

### 👤 Usuário

- Cadastro e autenticação com validação de e-mail por OTP  
- Redefinição de senha via e-mail  
- Alteração de perfil e senha  
- Participação em eventos e visualização da programação  

---

### 📅 Evento

- Criação, edição e exclusão de eventos  
- Listagem de eventos com filtros e busca  
- Confirmação de presença dos participantes  
- Geração de QR Code para entrada  
- Controle de acesso (liberação/trancamento de entrada)  

---

### 💬 Interações em Tempo Real

- Chat integrado nos eventos (mensagens públicas e privadas)  
- Identificação das mensagens por nome do usuário   
- Interface estilo sala de conferência com vídeo, chat e painel inferior  

---

### 📤 Comunicação

- Envio automático de e-mails com código OTP ou QR Code  
- Notificações visuais
- Compartilhamento de eventos em redes sociais  

---

## 🧠 Estrutura de Dados Aplicadas

- `ArrayList` e `Set` para listas de eventos, participantes e chats  
- `HashMap` para autenticação e relacionamentos rápidos  
- Lógica de busca, ordenação e filtragem manual  
- Controle de sessões em memória via classes singleton  

---

## 🗂️ Organização do Projeto

```plaintext
├── src/
│   ├── application/     → Ponto de entrada principal da aplicação
│   ├── model/           → Entidades do sistema (Usuario, Evento, Mensagem, etc.)
│   ├── otp/             → Envio de e-mails, lógica OTP e geração de QR Code
│   ├── resources/       → Ícones e imagens utilizados na aplicação
│   ├── service/         → Regras de negócio e manipulação de dados
│   ├── session/         → Gerenciamento de sessões e dados temporários
│   ├── view/            → Interfaces FXML e controladores das telas
│   └── module-info.java → Declaração dos módulos Java (JavaFX, JavaMail, etc.)
├── rec/                 → Repositório de bibliotecas externas (.jar)
```

## 📸 Exemplos Visuais

## 🚀 Como Executar

- Clone o repositório ->
 git clone https://github.com/Felipe-fMoura/Acelera-EvenMoreFun.git
- Abra o projeto na IDE Eclipse
  
**Configure o ambiente:**
- Tenha o Java 17+ instalado
- Configure o JavaFX SDK no module-path da aplicação
- Importe todos os arquivos .jar externos necessários (disponíveis na pasta /rec do repositório)
- Execute o Main.java -> /src/application/Main.java
