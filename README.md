# PUC-RIO - Inteligencia Artificial - T2 - Prolog

Instruções de Desenvolvimento:

-A interface irá tentar se ajustar aos dados estaticos da classe SINGLETONS no package Data

-A classe SINGLETONS armazena as variaveis a globais a serem atualizadas pelo Prolog, como ENERGIA/VIDA, SCORE, POSICAO e DIRECAO do personagem.

-Ainda na classe SINGLETONS, temos a classe GAMEGRID. Essa classe guarda as informações celula a celula. 

--GameGrid é da classe GRID, e possui dois metodos importantes:

---getCell( x , y ) : Obtem a celula na posicao x e y, ou null caso a posicao nao exista na grade

---getNeighbors( x , y ) : Obtem um arraylist de celulas visinhas da posição X e Y


-Cada instância de classe CELL em GRID deve ser atualizada pelo Prolog, pois lá ficam as informações se a celula já foi descober ou não, e se foi destruida/consumida ou não.


-No (defaultPackage) tem a classe APP. Ela basicamente é encarregada de iniciar a interface. As primeira chamada ao PROLOG deve entrar após a inicialização da interface, pois esta carrega os dados do mapa e preenche as inforçãoes do GRID e SINGLETONS.
