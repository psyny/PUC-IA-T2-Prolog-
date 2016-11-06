# PUC-RIO - Inteligencia Artificial - T2 - Prolog

## Instruções de Desenvolvimento:

- A interface irá tentar se ajustar aos dados estaticos da classe SINGLETONS no package Data

- A classe SINGLETONS armazena as variaveis a globais a serem atualizadas pelo Prolog, como ENERGIA/VIDA, SCORE, POSICAO e DIRECAO do personagem.

- Ainda na classe SINGLETONS, temos a classe GAMEGRID. Essa classe guarda as informações celula a celula. 

--GameGrid é da classe GRID, e possui dois metodos importantes:

---getCell( x , y ) : Obtem a celula na posicao x e y, ou null caso a posicao nao exista na grade

---getNeighbors( x , y ) : Obtem um arraylist de celulas visinhas da posição X e Y


- Cada instância de classe CELL em GRID deve ser atualizada pelo Prolog, pois lá ficam as informações se a celula já foi descober ou não, e se foi destruida/consumida ou não.


- No (defaultPackage) tem a classe APP. Ela basicamente é encarregada de iniciar a interface. As primeira chamada ao PROLOG deve entrar após a inicialização da interface, pois esta carrega os dados do mapa e preenche as inforçãoes do GRID e SINGLETONS.


- A classe "IVector2D" é utilizada diversas vezes. Ela é apenas uma estrutura que armazena X e Y.
Então new IVector2D(x',y') cria e iniciliza IVector2D.x como x' e IVector2D.y como y'

## Sobre o A-Star:

- O A* não  é instanciavel, é estatico ).

- Sua assinatura de uso é:  AStar.getPath( IVector2D destino )

Ele retorna uma classe chamada AStarPath, que contem 2 listas:

1)  AStar.cellList = lista de celulas do caminho, na ordem correta

2)  AStar.commandList = lista de comandos a dar ao PROLOG. Cada commando é do ENUM: Commands, do package PROLOG


- Como visto na assinatura, o A* necessita apenas do destino.

A origem é a posição do personagem armazenada em Singletons.heroPosition

Por isso deve-se sempre manter os singletons atualizados ( topico seguinte )

- Exemplo de uso:

AStarPath asp = AStar.getPath( new IVector2D(10,0) );

Retorna o caminho da posição do personagem até a celula X = 10 e Y = 0


## O que deve ser atualizado SEMPRE depois de uma ação do PROLOG

### TODAS AS VARIAVEIS SEGUIR SÃO ESTATICAS DA CLASSE "SINGLETONS"

- int heroLife = life atual. Importancia: BAIXA: interface

- int heroScore = pontuacao atual. Importancia: BAIXA: interface

- IVector2D heroPosition = posicao atual do personagem. Importancia: ALTA: Base do A*

- int heroDirection = direcao atual do personagem. Importancia: ALTA: Dado do A*
Sendo: 1 = Virado para cima. 2 = Virado para Direita. 3 = Virado para baixo. 4 = Virado para Esquerda

- Grid gameGrid = estado do mapa. Importancia: ALTA: Base das decisões e do A*
Deve-se atualizar as informações das celulas. ( Topico seguinte )

### GAME GRID

Metodo para obter celula:

- getCell( x , y ) : Obtem a celula na posicao x e y, ou null caso a posicao nao exista na grade

### CELL

Variaveis a serem atualizadas:

- boolean discovery = define se a celula ja foi descoberta pelo Heroi/Prolog. Importancia: ALTA: Base do A* e das decisões

- boolean destroyed = define se a celula teve seu conteudo destruido ( inimigo, vida, etc ). Importancia: ALTA: Base do A* e das decisões
