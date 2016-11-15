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

terminou(nao).

acao(decidir) :-	total_ouros(X), X =< 0, findall(_,retract(proximo_passo(_,_)),_), findall(_, encontrar_mais_proximo(certeza(saida)) ,_), !.
acao(decidir) :-	posicao(Z, W), mapa_agente((Z, W), certeza(ouro)), acao(pegar_objeto), acao(decidir), !.
acao(decidir) :-	energia(V), V =< 50, ( posicao(Z, W), mapa_agente((Z, W), certeza(power_up)), acao(pegar_objeto), acao(decidir) ; (mapa_agente((_, _), certeza(power_up)), findall(_,retract(proximo_passo(_,_)),_), findall(_, encontrar_mais_proximo(certeza(power_up)) ,_ ) , !) ).
acao(decidir) :-	agente_olhando_para(X, Y), posicao(Z, W), mapa_agente((X, Y), duvida(nada)), distancia_manhatam((Z,W), (X, Y), Custo), limpa_proximo_passo(), adiciona_proximo_passo((X, Y), Custo), !.
acao(decidir) :-	( mapa_agente((_,_), duvida(nada)), limpa_proximo_passo(), findall(_, encontrar_mais_proximo(duvida(nada)) ,_ )  ), !.

acao(mover_para_frente) :- 	orientacao(direita), posicao(X, Y), Z is X + 1, W is Y, posicao_pertence_mapa(Z, W), atualiza_posicao(Z, W), ( (mapa_real((Z, W), Objeto), assert_certeza_local((Z, W), Objeto)); assert_certeza_local((Z, W), nada) ), adiciona_a_score(-1), transcore_eventos(), ! .
acao(mover_para_frente) :-	orientacao(esquerda), posicao(X, Y), Z is X - 1, W is Y, posicao_pertence_mapa(Z, W), atualiza_posicao(Z, W), ( (mapa_real((Z, W), Objeto), assert_certeza_local((Z, W), Objeto)); assert_certeza_local((Z, W), nada) ), adiciona_a_score(-1), transcore_eventos(), ! .
acao(mover_para_frente) :-	orientacao(cima), posicao(X, Y), Z is X, W is Y + 1, posicao_pertence_mapa(Z, W), atualiza_posicao(Z, W), ( (mapa_real((Z, W), Objeto), assert_certeza_local((Z, W), Objeto)); assert_certeza_local((Z, W), nada) ), adiciona_a_score(-1), transcore_eventos(), ! .
acao(mover_para_frente) :-	orientacao(baixo), posicao(X, Y), Z is X, W is Y - 1, posicao_pertence_mapa(Z, W), atualiza_posicao(Z, W), ( (mapa_real((Z, W), Objeto), assert_certeza_local((Z, W), Objeto)); assert_certeza_local((Z, W), nada) ), adiciona_a_score(-1), transcore_eventos(), ! .

acao(virar_a_direita) :- orientacao(cima), retract(orientacao(cima)), assert(orientacao(direita)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(esquerda), retract(orientacao(esquerda)), assert(orientacao(cima)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(baixo), retract(orientacao(baixo)), assert(orientacao(esquerda)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(direita), retract(orientacao(direita)), assert(orientacao(baixo)), adiciona_a_score(-1), !.

acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), ouro), findall(_,assert_certeza_local( (X, Y), nada),_) , retract(mapa_real((X, Y), ouro)), assert(mapa_real((X, Y), nada)), adiciona_a_score(1000), total_ouros(O), retract(total_ouros(_)), K is O - 1, assert(total_ouros(K)), !.
acao(pegar_objeto) :- posicao(X, Y), mapa_real((X, Y), power_up), findall(_,assert_certeza_local( (X, Y), nada),_), retract(mapa_real((X, Y), power_up)), assert(mapa_real((X, Y), nada)), recuperar_vida(), !. 

acao(subir) :-		posicao(X, Y), X = 1, Y = 1, assert(terminou(saiu_do_labirinto)), write('O Agente saiu da caverna, que fim glorioso').

acao(atirar) :-		municao(X), X > 0, adiciona_a_score(-10), findall(_,simula_tiro(),_), remove_municao().

encontrar_mais_proximo(Objeto) :-	findall(_,menor_distancia(Objeto),_), !.

menor_distancia(Objeto) :-	mapa_agente((X, Y), Objeto), posicao(Z, W), distancia_manhatam((Z,W), (X, Y), Custo) , adiciona_proximo_passo((X, Y), Custo).

observa(X, Y) :-	((Z is X + 1, W is Y, posicao_pertence_mapa(Z, W), (mapa_real( (Z, W) , Objeto), Objeto \= ouro, Objeto \= power_up), atualiza_mapa_agente( (X, Y), Objeto) ) ;
					(Z is X - 1, W is Y, posicao_pertence_mapa(Z, W), (mapa_real( (Z, W) , Objeto), Objeto \= ouro, Objeto \= power_up),  atualiza_mapa_agente( (X, Y), Objeto) ) ;
					(Z is X, W is Y + 1, posicao_pertence_mapa(Z, W), (mapa_real( (Z, W) , Objeto), Objeto \= ouro, Objeto \= power_up), atualiza_mapa_agente( (X, Y), Objeto) ) ;
					(Z is X, W is Y - 1, posicao_pertence_mapa(Z, W), (mapa_real( (Z, W) , Objeto), Objeto \= ouro, Objeto \= power_up), atualiza_mapa_agente( (X, Y), Objeto) ) ).
						
observa(X, Y) :-	not((Z is X + 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is X - 1, posicao_pertence_mapa(Z, Y), mapa_real( (Z, Y) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is Y + 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up;
						Z is Y - 1, posicao_pertence_mapa(X, Z), mapa_real( (X, Z) , Objeto), Objeto \= nada, Objeto \= ouro, Objeto \= power_up)) , findall(_,atualiza_mapa_agente((X, Y), nada),_), !.
	

assert_certeza_local( (X, Y), Objeto ) :-	findall(_,retract(mapa_agente( (X, Y), duvida(_) )),_), assert(mapa_agente((X, Y), certeza(Objeto))), findall(_,remove_sensor_destino((X, Y), _),_), !.

assert_certeza_deduzida( (X, Y), Objeto ) :- Objeto \= nada , Objeto \= power_up , Objeto \= ouro , findall(_,retract(mapa_agente( (X, Y), duvida(_) )),_), assert(mapa_agente((X, Y), certeza(Objeto))), ( remove_sensor_destino_semRevalidar((X, Y),Objeto) ; true ) , ( remove_sensor_destino_all((X, Y),Objeto) ; true ).
	
	
	
atualiza_mapa_agente( (X, Y), nada) :- 		(Z is X + 1, W is Y, posicao_pertence_mapa(Z, W), not(mapa_agente( (Z, W) , certeza(_))), findall(_,retract(mapa_agente((Z, W) , duvida(_))),_) ,  assert(mapa_agente( (Z, W) , duvida(nada) )) ;
											Z is X - 1, W is Y, posicao_pertence_mapa(Z, W), not(mapa_agente( (Z, W) , certeza(_))), findall(_,retract(mapa_agente((Z, W) , duvida(_))),_) , assert(mapa_agente( (Z, W) , duvida(nada) )) ;
											Z is X, W is Y + 1, posicao_pertence_mapa(Z, W), not(mapa_agente( (Z, W) , certeza(_))), findall(_,retract(mapa_agente((Z, W) , duvida(_))),_) , assert(mapa_agente( (Z, W) , duvida(nada) )) ;
											Z is X, W is Y - 1, posicao_pertence_mapa(Z, W), not(mapa_agente( (Z, W) , certeza(_))), findall(_,retract(mapa_agente((Z, W) , duvida(_))),_) , assert(mapa_agente( (Z, W) , duvida(nada) ))) .


	
atualiza_mapa_agente( (X, Y), Objeto) :- 	(Z is X + 1, W is Y, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), adicionar_sensor((X, Y), (Z,W), Objeto) ) ;
											Z is X - 1, W is Y, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), adicionar_sensor((X, Y), (Z, W), Objeto) ) ;
											Z is X, W is Y + 1, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), adicionar_sensor((X, Y), (Z, W), Objeto) ) ;
											Z is X, W is Y - 1, posicao_pertence_mapa(Z, W), ( not(mapa_agente( (Z, W) , certeza(_))) , not(mapa_agente( (Z, W) , duvida(nada))), adicionar_sensor((X, Y), (Z, W), Objeto) ) ).
											
atualiza_mapa_agente( (X, Y), _) :- 	(Z is X + 1, posicao_pertence_mapa(Z, Y), not(sensor((X,Y),(Z,Y),_)) , not(mapa_agente((Z, Y) , _)), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
										Z is X - 1, posicao_pertence_mapa(Z, Y), not(sensor((X,Y),(Z,Y),_)) , not(mapa_agente((Z, Y) , _)), assert(mapa_agente( (Z, Y) , duvida(nada) )) ;
										Z is Y + 1, posicao_pertence_mapa(X, Z), not(sensor((X,Y),(X,Z),_)) , not(mapa_agente((X, Z) , _)), assert(mapa_agente( (X, Z) , duvida(nada) )) ;
										Z is Y - 1, posicao_pertence_mapa(X, Z), not(sensor((X,Y),(X,Z),_)) , not(mapa_agente((X, Z) , _)), assert(mapa_agente( (X, Z) , duvida(nada) )) ).
								
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = buraco, adiciona_a_score(-1000), assert(terminou(morreu_para_buraco)), write('Agente Morreu caindo em um buraco, mas que fim tragico'), observa(X, Y).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = inimigo(D, _), findall(_,tomar_dano(D),_), observa(X, Y).
transcore_eventos() :-	posicao(X, Y), mapa_real((X, Y), Objeto), Objeto = teleporte, tamanho_mapa(Tx, Ty), Z is (random(Tx - 1) + 1), W is (random(Ty - 1) + 1), retract(posicao(_,_)), assert(posicao(Z, W)), observa(X, Y), transcore_eventos().
transcore_eventos() :-	posicao(X, Y), findall(_,observa(X, Y),_), findall(_,valida_sensor( (X,Y) , _ ),_).

simula_tiro() :- agente_olhando_para(X, Y), mapa_real((X, Y), inimigo(_, _)), dar_dano_inimigo(X, Y).

dar_dano_inimigo(X, Y) :-	mapa_real((X, Y), inimigo(D, V)), Dano is (random(30) + 20), W is V - Dano, retract(mapa_real((X, Y), inimigo(_, _))), ( ( W > 0, assert(mapa_real((X, Y), inimigo(D, W))) ) ; findall(_,assert_certeza_local( (X, Y), nada),_) ), ! .

agente_olhando_para(X, Y) :-	orientacao(cima), posicao(Z, W), X is Z, Y is W + 1, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(baixo), posicao(Z, W), X is Z, Y is W - 1, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(direita), posicao(Z, W), X is Z + 1, Y is W, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(esquerda), posicao(Z, W), X is Z - 1, Y is W, (posicao_pertence_mapa(X, Y) ; write('Parede')), !.
												
adiciona_a_score(X) :- score(Y), W is Y + X, findall(_,retract(score(_)),_), assert(score(W)).

remove_municao() :-	municao(Y), W is Y - 1, retract(municao(_)), assert(municao(W)), !.

recuperar_vida() :- energia(Y), W is Y + 20, (W =< 100; W is 100), retract(energia(_)), assert(energia(W)).

tomar_dano(X) :-	energia(Y), W is Y - X, retract(energia(_)), assert(energia(W)), adiciona_a_score(-X).
tomar_dano(_) :-	energia(W), W =< 0, adiciona_a_score(-1000), assert(terminou(morreu_para_inimigo)), write('Agente Morreu para um inimigo, , mas que fim tragico').

valida_sensor((X, Y), Objeto) :- sensor((X, Y), (Z, W), Objeto), not((sensor((X, Y), (Xi, Yi), Objeto), (Xi \= Z ; Yi \= W))), assert_certeza_deduzida((Z, W), Objeto).

adicionar_sensor((X, Y), (Z, W), Objeto) :-	Objeto \= nada, Objeto \= power_up , Objeto \= ouro , not(adjacente_a((X, Y), Objeto)) , not(sensor((X, Y), (Z, W), Objeto) ) , assert(sensor((X, Y), (Z, W), Objeto)),!.

remove_sensor((X, Y),(Z, W), Objeto) :-	 sensor((X, Y), (Z, W), Objeto), retract(sensor((X,Y), (Z, W), Objeto)) , valida_sensor((X, Y), Objeto).

remove_sensor_destino((X, Y), Objeto) :-	sensor((Z, W), (X, Y), Objeto), remove_sensor((Z, W),(X, Y), Objeto) , valida_sensor((Z, W), Objeto).

remove_sensor_destino_all_aux((X, Y), Objeto) 	:-  ObjetoNovo \= Objeto , ( findall(_,remove_sensor_destino((X, Y), ObjetoNovo),_) ; true ).
remove_sensor_destino_all((X, Y), Objeto) 		:-	findall(_,remove_sensor_destino_all_aux((X, Y), Objeto),_).

remove_sensor_forca_bruta( (X, Y),(Z, W), Objeto ) :-  retract(sensor((X,Y), (Z, W) , Objeto ) ) , ( not(sensor( (X,Y) , (_,_) , _ ) ) , atualiza_mapa_agente( ( X , Y ) , nada ) ).

remove_sensor_semRevalidar((X, Y),(Z, W), Objeto) :-	sensor((X,Y), (Z, W), Objeto), findall(_, remove_sensor_forca_bruta((X,Y), (_, _), Objeto)  ,_ ).

remove_sensor_destino_semRevalidar((X, Y), Objeto) :-	findall(_, remove_sensor_semRevalidar((_,_),(X, Y), Objeto) , _ ).

atualizar_minimo(X) :-	findall(_,retract(minimo(_)),_), assert(minimo(X)).

adiciona_proximo_passo((X, Y), Custo) :- assert(proximo_passo((X,Y), Custo)).

limpa_proximo_passo() :- findall(_,retract(proximo_passo(_,_)),_).

atualiza_posicao(X, Y) :-	posicao(Z, W), retract(posicao(Z, W)), assert(posicao(X, Y)).

instanciar_tamanho_mapa(X, Y) :-	assert(tamanho_mapa(X, Y)).

adicionar_ao_mapa((X, Y), Objeto) :- assert(mapa_real( (X, Y) , Objeto)).

distancia_manhatam((X1,Y1), (X2, Y2), C) :-	mod(X1 - X2, X), mod(Y1 - Y2, Y), C is (X + Y).

mod(X, Y) :- (X < 0, Y is (-X) , !) ; (Y is X , !).
												
posicao_pertence_mapa(X, Y) :- tamanho_mapa(A, B), X < A, X >= 0, Y < B, Y >= 0.	

adjacente_a((X, Y), Objeto) :- 	Z is X + 1, W is Y, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X - 1, W is Y, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X, W is Y + 1, mapa_agente((Z, W), certeza(Objeto)) , ! .
adjacente_a((X, Y), Objeto) :-	Z is X, W is Y - 1, mapa_agente((Z, W), certeza(Objeto)) , ! .								