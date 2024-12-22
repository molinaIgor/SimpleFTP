SimpleFTPServer

Descrição

O SimpleFTPServer é uma implementação básica de um servidor FTP em Java. Ele suporta os comandos FTP fundamentais, como:

- USER: Autenticação do nome de usuário.
- PASS: Autenticação de senha.
- LIST: Listagem de arquivos fictícios.
- QUIT: Encerramento da conexão.
O servidor utiliza sockets padrão do Java, sem dependências externas, tornando-o simples e direto para aprendizado e demonstrações.

Recursos Suportados
Autenticação Básica: Usuário e senha predefinidos ("user" e "pass").
Modo Passivo: Transferência de dados em um socket separado para comandos como LIST.
Mensagens de Resposta: Respostas formatadas no padrão FTP para interação com clientes FTP.

Estrutura do Código

- Servidor de Controle
- Escuta conexões na porta de controle (2121).
- Aceita conexões de múltiplos clientes, cada uma tratada em sua própria thread.

Autenticação Processa os comandos USER e PASS para autenticar o cliente.

Comandos

- LIST: Retorna uma lista fictícia de arquivos usando uma conexão de dados.

- QUIT: Fecha a conexão com o cliente.

A Conexão de Dados utiliza sockets para transferência de arquivos em modo passivo.

Use um cliente FTP (como FileZilla ou ftp no terminal) para conectar ao servidor:

Host: 127.0.0.1
Porta: 2121
Usuário: user
Senha: pass

Exemplos de Comandos no Cliente FTP

Autenticação:
USER user
PASS pass

Listar Arquivos:
LIST

Encerrar Conexão:
QUIT

Melhorias Futuras

- Implementar suporte a comandos adicionais como RETR (download de arquivos) e STOR (upload de arquivos).

- Permitir configuração dinâmica de usuários e senhas.

- Adicionar suporte a um sistema de arquivos real para manipulação de arquivos.

- Implementar logs detalhados para auditoria e depuração.