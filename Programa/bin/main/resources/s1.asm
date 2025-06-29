# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    contador_var: .word 0
    num_var: .word 0
    x_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s1_intermediate.txt
sumarDos:
    # Prólogo simplificado sumarDos
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Guardar parámetros para sumarDos
    sw $a0, x_var
    # Parámetro x guardado

    # Inicio de función
    # DECLARE x INT
    # t1 = x + 2
    lw $t1, x_var
    li $t2, 2
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE resultado INT
    # resultado = t1
    lw $t0, t1_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_sumarDos


# Epílogo simplificado sumarDos
exit_sumarDos:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # PARAM 10
    li $a0, 10
    # ✅ s1: Parámetro 10 cargado en $a0

    # t2 = CALL sumarDos 1
    jal sumarDos
    sw $v0, t2_var

    # DECLARE num INT
    # num = t2
    lw $t0, t2_var
    sw $t0, num_var

    # WRITE num
    lw $a0, num_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo simplificado main
exit_main:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

