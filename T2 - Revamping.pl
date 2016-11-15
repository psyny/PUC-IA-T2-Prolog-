:-	dynamic proximo_passo/2.
:-	dynamic tamanho_mapa/2.
:-	dynamic mapa_real/2.
:-	dynamic inimigo/2.
:-	dynamic total_ouros/1.
:-	dynamic terminou/1.
:-	dynamic sensor/3.
:-	dynamic posicao/2.
:-	dynamic orientacao/1.
:-	dynamic score/1.
:-	dynamic municao/1.
:-	dynamic energia/1.
:-	dynamic mapa_agente/2.


:- 	dynamic	fronteira/2.
:-  dynamic certeza/2.

terminou(nao).

acao(decidir) :-	total_ouros(X), X =< 0, findall(_,retract(proximo_passo(_,_)),_),  adiciona_proximo_passo((X, Y), 0) , !.
acao(decidir) :-	posicao(X, Y), certeza( ( X , Y ) , ouro ) , acao(pegar_objeto), acao(decidir), !.
acao(decidir) :-	energia(V), V =< 50, ( posicao(X, Y), certeza( ( X , Y ) , power_up ) , acao(pegar_objeto) , acao(decidir) ; ( certeza( ( _ , _ ) , power_up ) , limpa_proximo_passo() , findall(_, encontrar_mais_proximo(power_up) ,_ ) , !) ).
acao(decidir) :-	agente_olhando_para(X, Y) , posicao(Z, W) , fronteira(X,Y) , limpa_proximo_passo() , adiciona_proximo_passo((X, Y), 1) , !.
acao(decidir) :-	( fronteira(_,_) , limpa_proximo_passo(), findall(_, encontrar_mais_proximo() ,_ )  ), !.

acao(mover_para_frente) :- 	mover(Z,W) , adiciona_a_score(-1) , ( consequencias(Z,W) ; true ) , observar(Z,W) , !.

acao(virar_a_direita) :- orientacao(cima), retract(orientacao(cima)), assert(orientacao(direita)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(esquerda), retract(orientacao(esquerda)), assert(orientacao(cima)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(baixo), retract(orientacao(baixo)), assert(orientacao(esquerda)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(direita), retract(orientacao(direita)), assert(orientacao(baixo)), adiciona_a_score(-1), !.


/** ATE AQUI FOI FEITA */



acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), ouro), findall(_,assert_certeza_local( (X, Y), nada),_) , retract(mapa_real((X, Y), ouro)), assert(mapa_real((X, Y), nada)), adiciona_a_score(1000), total_ouros(O), retract(total_ouros(_)), K is O - 1, assert(total_ouros(K)), !.
acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), power_up), findall(_,assert_certeza_local( (X, Y), nada),_), retract(mapa_real((X, Y), power_up)), assert(mapa_real((X, Y), nada)), recuperar_vida(), !. 

acao(subir) :-		posicao(X, Y), X = 1, Y = 1, assert(terminou(saiu_do_labirinto)), write('O Agente saiu da caverna, que fim glorioso').

acao(atirar) :-		municao(X), X > 0, adiciona_a_score(-10), findall(_,simula_tiro(),_), remove_municao().


									
//TRADUZIR PARA A FUNCAO "CONSEQUENCIAS" MAIS ABAIXP							
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = buraco, adiciona_a_score(-1000), assert(terminou(morreu_para_buraco)), write('Agente Morreu caindo em um buraco, mas que fim tragico'), observa(X, Y).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = inimigo(D, _), findall(_,tomar_dano(D),_), observa(X, Y).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = teleporte, tamanho_mapa(Tx, Ty), Z is (random(Tx - 1) + 1), W is (random(Ty - 1) + 1), retract(posicao(_,_)), assert(posicao(Z, W)), observa(X, Y), transcore_eventos().
transcore_eventos() :-	posicao(X, Y), findall(_,observa(X, Y),_), findall(_,valida_sensor( (X,Y) , _ ),_).





simula_tiro() :- agente_olhando_para(X, Y), mapa_real((X, Y), inimigo(_, _)), dar_dano_inimigo(X, Y).

dar_dano_inimigo(X, Y) :-	mapa_real((X, Y), inimigo(D, V)), Dano is (random(30) + 20), W is V - Dano, retract(mapa_real((X, Y), inimigo(_, _))), ( ( W > 0, assert(mapa_real((X, Y), inimigo(D, W))) ) ; findall(_,assert_certeza_local( (X, Y), nada),_) ), ! .

agente_olhando_para(X, Y) :-	orientacao(cima), posicao(Z, W), X is Z, Y is W + 1, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(baixo), posicao(Z, W), X is Z, Y is W - 1, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(direita), posicao(Z, W), X is Z + 1, Y is W, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(esquerda), posicao(Z, W), X is Z - 1, Y is W, (eh_no_mapa(X, Y) ; write('Parede')), !.
												
adiciona_a_score(X) :- score(Y), W is Y + X, findall(_,retract(score(_)),_), assert(score(W)).

remove_municao() :-	municao(Y), W is Y - 1, retract(municao(_)), assert(municao(W)), !.

recuperar_vida() :- energia(Y), W is Y + 20, (W =< 100; W is 100), retract(energia(_)), assert(energia(W)).

tomar_dano(X) :-	energia(Y), W is Y - X, retract(energia(_)), assert(energia(W)), adiciona_a_score(-X).
tomar_dano(_) :-	energia(W), W =< 0, adiciona_a_score(-1000), assert(terminou(morreu_para_inimigo)), write('Agente Morreu para um inimigo, , mas que fim tragico').




instanciar_tamanho_mapa(X, Y) :-	assert(tamanho_mapa(X, Y)).




consequencias() 		:- posicao(X, Y) , possui_objeto( X , Y , Objeto ) , 




/** REGIAO NOVA! */

observar( X , Y )		:- ( definir_certeza_local( X , Y ) ; true ) , ( criar_sensores_em( X , Y ) ; true ) , ( validar_sensores_em( X , Y ) ; true ) , ( validar_sensores_adjacentes( X , Y ) ; true ) , ( adicionar_fronteiras_em( X , Y ) ; true ).



adicionar_fronteiras_em( X , Y )									:- findall( _ , intern_fronteira_cond( X , Y ) , _ ).

intern_fronteira_cond( X , Y )										:- eh_adjacente( X , Y , X2 , Y2 ) , not(eh_conhecido( X2 , Y2 )) , not(eh_perigosa( X2 , Y2 )) , assert( fronteira( X2 , Y2 ) ).


remover_sensor_completamente( PX , PY , Objeto )					:- findall( _ , retract( sensor( (PX,PY) , (_,_) , Objeto ) ) , _ ).

remover_parcialmente_sensor( TX , TY , Objeto )						:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , remover_parcialmente_sensor( PX , PY , TX , TY , Objeto ).
remover_parcialmente_sensor( PX , PY , TX , TY , Objeto )			:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , retract( ( PX , PY ) , ( TX , TY ) , Objeto ) , validar_sensores_em( PX , PY ).


definir_certeza_local( X , Y )										:- ( possui_objeto( X , Y , Objeto ) ; Objeto is nada ) , assert( certeza( ( X , Y ) , Objeto ) ) , findall( _ , remover_parcialmente_sensor( X , Y , _ ) , _ ).

definir_certeza_deduzida( X , Y , Objeto )							:- assert( certeza( ( X , Y ) , Objeto ) ) , findall( _ , intern_remover_completamente_sensor_com_alvo( X , Y , Objeto ) , _ ) , findall( _ , intern_remover_parcialmente_sensor( X , Y , Objeto ) , _ ).

intern_remover_completamente_sensor_com_alvo( TX , TY , Objeto )	:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , remover_sensor_completamente( PX , PY , Objeto ).

intern_remover_parcialmente_sensor( TX , TY , Objeto )				:- ObjetoAlvo \= Objeto , remover_parcialmente_sensor( TX , TY , ObjetoAlvo ).



validar_sensores_adjacentes( X , Y )						:- findall( _ , intern_validar_sensores_adjacentes_cond( X , Y  ) , _ ).

intern_validar_sensores_adjacentes_cond( X , Y )			:- eh_adjacente( X , Y , X2 , Y2 ) , sensor( ( X3 , Y3 ) , ( X2 , Y2 ) , Objeto ) , ( X3 \= X , Y3 \= Y ) , not( sensor( (X,Y) , (X2,Y2) , Objeto ) ) , remover_parcialmente_sensor( X3 , Y3 , X2 , Y2 , Objeto );



criar_sensores_em( X , Y )  						:- findall( _ , intern_criar_sensores_perigos_em( X , Y ) , _ ).

intern_criar_sensores_perigos_em( X , Y )  			:- eh_adjacente( X , Y , X2 , Y2 ) , eh_no_mapa( X2 , Y2 )  ,  not( eh_conhecido( X2 , Y2 ) )  ,  eh_ameaca( X2 , Y2 )  ,  possui_objeto( X2 , Y2 , Objeto )  ,  findall(_ , intern_criar_sensores_cond( X , Y ) , _ ).

intern_criar_sensores_cond( X , Y , Objeto )		:- eh_adjacente( X , Y , X2 , Y2 ) , eh_no_mapa( X2 , Y2 )  ,  not( eh_conhecido( X2 , Y2 ) )  ,  not( eh_fronteira( X2 , Y2 ) )  ,  intern_criar_sensor( X , Y , X2 , Y2 , Objeto ).

intern_criar_sensor( PX , PY , TX , TY , Objeto )	:-	not( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ) , assert( sensor( PX , PY , TX , TY ) ).



validar_sensores_em( X , Y )					:- findall( _ , intern_validar_sensores_em( X , Y ) , _ ).

intern_validar_sensores_em( X , Y )				:- sensor((X, Y), (TX, TY), Objeto), not((sensor((X, Y), (Xi, Yi), Objeto), (Xi \= TX ; Yi \= TY))) , definir_certeza_deduzida( TX , TY , Objeto ).







eh_conhecido( X , Y ) 				:- certeza( ( X , Y ) , _ ).

eh_fronteira( X , Y ) 				:- fronteira( X , Y ).

eh_perigosa( X , Y ) 				:- sensor( (_,_) , (X,Y) , _ ).

eh_no_mapa(X, Y) 					:- tamanho_mapa(A, B), X < A, X >= 0, Y < B, Y >= 0.	

eh_adjacente( X , Y , X2 , Y2 )		:- X2 = X + 1 , Y2 = Y , !.
eh_adjacente( X , Y , X2 , Y2 )		:- X2 = X - 1 , Y2 = Y , !.
eh_adjacente( X , Y , X2 , Y2 )		:- X2 = X , Y2 = Y + 1 , !.
eh_adjacente( X , Y , X2 , Y2 )		:- X2 = X , Y2 = Y - 1 , !.

eh_ameaca( Objeto )					:- Objeto \= power_up , Objeto \= ouro , Objeto \= nada. 
eh_ameaca( X , Y )					:-	mapa_real( ( X , Y ) , Objeto ) , eh_ameaca( Objeto ).





possui_objeto( X , Y , Objeto )		:-	mapa_real( ( X , Y ) , Objeto ).

distancia_manhatam((X1,Y1), (X2, Y2), C) :-	mod(X1 - X2, X), mod(Y1 - Y2, Y), C is (X + Y).

mod(X, Y) :- (X < 0, Y is (-X) , !) ; (Y is X , !).

atualizar_posicao(X, Y) :-	posicao(Z, W), retract(posicao(Z, W)), assert(posicao(X, Y)).

adiciona_proximo_passo((X, Y), Custo) :- assert(proximo_passo((X,Y), Custo)).

limpa_proximo_passo() :- findall(_,retract(proximo_passo(_,_)),_).

encontrar_mais_proximo() :-	findall(_,menor_distancia(),_), !.
encontrar_mais_proximo( Objeto ) :-	findall(_,menor_distancia( Objeto ),_), !.

menor_distancia() 			:-	fronteira( X , Y ) , posicao(Z, W), distancia_manhatam((Z,W), (X, Y), Custo) , adiciona_proximo_passo((X, Y), Custo).
menor_distancia( Objeto ) 	:-	certeza( ( X , Y ) , Objeto ) , posicao(Z, W), distancia_manhatam((Z,W), (X, Y), Custo) , adiciona_proximo_passo((X, Y), Custo).

mover(Z,W)		:- orientacao(direita), posicao(X, Y), Z is X + 1, W is Y, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(esquerda), posicao(X, Y), Z is X - 1, W is Y, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(cima), posicao(X, Y), Z is X, W is Y + 1, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(baixo), posicao(X, Y), Z is X, W is Y - 1, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.





extern_adicionar_ao_mapa((X, Y), Objeto) :- assert( certeza( ( X , Y ) , Objeto ) ).


							